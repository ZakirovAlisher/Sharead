package com.example.site.controller;

import com.example.site.domain.*;
import com.example.site.service.*;
import com.example.site.util.BooksCounterDTO;
import com.example.site.util.ExchangeStatus;
import com.example.site.util.ExchangeWebRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class ExchangeController {

    @Autowired
    SendEmailService sendEmailService;

    @Autowired
    ExchangeWebRequest exchangeWebRequest;

    @Autowired
    private UserService userService;

    @Autowired
    private BookService bookService;

    @Autowired
    private UserBookService userBookService;

    @Autowired
    private ViewHistoryService viewHistoryService;

    @Value("${file.book.viewPath}")
    private String viewPath;

    @Autowired
    private ExchangeService exchangeService;

    @Autowired
    private OfferService offerService;

    @GetMapping(value = "/")
    public String exchanges(Model model){

        Set<Books> userPreferredBooks = new HashSet<>();

        List<Exchanges> openedExchanges = this.exchangeService.getExchangesByStatus(ExchangeStatus.OPENED);

        Map<Exchanges, Integer> recommendedExchanges = new HashMap<>();

        for (ViewHistory v2: Objects.requireNonNull(getUserData()).getViewHistories()) {
                userPreferredBooks.add(v2.getBook());
            }
        List<Users> usersWithSameViews =  viewHistoryService.getUsersWithSameViews(getUserData(), userPreferredBooks);

        List<BooksCounterDTO> othersPrefferedBooks = viewHistoryService.getOthersPrefferedBooks(usersWithSameViews , userPreferredBooks);
        Map<Long, Integer> othersPrefferedBooksMap = othersPrefferedBooks.stream().collect(
                Collectors.toMap(BooksCounterDTO::getBook_id, BooksCounterDTO::getCounter));
        for (Exchanges e: openedExchanges) {
            Integer exchangeCounter = 0;
            for (UserBooks ub: e.getUserBooks()) {
                Books book = ub.getBook();
                Integer counter = othersPrefferedBooksMap.get(book.getId());
                if (counter != null){
                    exchangeCounter += counter;
                }
            }
            recommendedExchanges.put(e, exchangeCounter);
        }
        recommendedExchanges = MapUtil.sortByValue(recommendedExchanges);

        System.out.println("DEBUG");
        for (Map.Entry <Exchanges, Integer> e: recommendedExchanges.entrySet()
             ) {
            System.out.println(e.getValue());
        }

        model.addAttribute("currentUser", getUserData());
        model.addAttribute("exchangesOpen", recommendedExchanges.keySet());
        model.addAttribute("exchangesFreeze",
                           this.exchangeService.getExchangesByStatus(ExchangeStatus.FREEZED));

        return "exchanges";
    }

    public class MapUtil {
        public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
            List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
            list.sort(Map.Entry.comparingByValue());
            Collections.reverse(list);
            Map<K, V> result = new LinkedHashMap<>();
            for (Map.Entry<K, V> entry : list) {
                result.put(entry.getKey(), entry.getValue());
            }

            return result;
        }
    }

    @GetMapping(value = "/exchangeDetails/{id}")
    public String exchangeDetails(Model m , @PathVariable(name = "id") Long id,
                                  @RequestParam(name="searchUserStr",required = false) String searchUserStr){
        List<UserBooks> userBooks = new ArrayList<>();

        if (searchUserStr == null || searchUserStr.equals("")){
            userBooks = this.userBookService.getAllBooksByUser(getUserData());
        }
        if (searchUserStr != null){
            userBooks = this.userBookService.getAllUserBooksSearch(searchUserStr, getUserData());
        }

        List<UserBooks> currentOfferBooks = new ArrayList<>();


        Exchanges exchange = this.exchangeService.getExchange(id);

        for (UserBooks b:
             exchange.getUserBooks()) {
            ViewHistory viewHistory = viewHistoryService.getViewHistoriesByUserAndBook(getUserData(), b.getBook());
            if(viewHistory != null){
                viewHistory.setCounter(viewHistory.getCounter()+1);
                viewHistoryService.saveViewHistory(viewHistory);
            }
            else{
                viewHistoryService.addViewHistory(new ViewHistory(null, getUserData(), b.getBook(), 1));
            }
        }


        for (Long bookId:this.exchangeWebRequest.getOfferBooks()) {
            UserBooks userBook = this.userBookService.getBook(bookId);
            currentOfferBooks.add(userBook);
            userBooks.remove(userBook);
        }

        boolean myExchange = false;
        boolean canOffer = false;
        if (!exchange.getUser().getId().equals(getUserData().getId()) && !exchange.getStatus().equals(ExchangeStatus.FREEZED)
                && !exchange.getStatus().equals(ExchangeStatus.CLOSED)
        ){
            canOffer = true;
        }
        if (exchange.getUser().getId().equals(getUserData().getId()) && !exchange.getStatus().equals(ExchangeStatus.FREEZED)
                && !exchange.getStatus().equals(ExchangeStatus.CLOSED)){
            myExchange = true;
        }

        m.addAttribute("exchange", exchange);
        m.addAttribute("offers", this.offerService.getByExchange(exchange));
        m.addAttribute("currentOfferBooks", currentOfferBooks);
        m.addAttribute("userBooks", userBooks);
        m.addAttribute("myExchange", myExchange);
        m.addAttribute("canOffer", canOffer);

        return "exchangeDetails";
    }

    @PostMapping(value = "/postOffer")
    public String addOfferToExchange(
            @RequestParam(name = "exchange_id") Long exchangeId,
            @RequestParam(name = "comment") String comment,
            RedirectAttributes redirAttrs) {

        if (this.exchangeWebRequest.getOfferBooks().isEmpty() && comment.equals("")){
            redirAttrs.addFlashAttribute("errorA", "Offer can't be empty");
            return "redirect:/exchangeDetails/" + exchangeId  + "#pickUserBooks";
        }

        List<UserBooks> offerBooks = new ArrayList<>();

        for (Long id:this.exchangeWebRequest.getOfferBooks()) {
            UserBooks userBook = this.userBookService.getBook(id);
            offerBooks.add(userBook);
        }

        Offers offer = new Offers();
        offer.setExchange(this.exchangeService.getExchange(exchangeId));
        offer.setComment(comment);
        offer.setDate(new Date());
        offer.setUser(getUserData());
        offer.setUserBooks(offerBooks);

        this.offerService.saveOffer(offer);
        this.exchangeWebRequest.setOfferBooks(new ArrayList<>());

        redirAttrs.addFlashAttribute("successA", "Offer successfully posted");

        return "redirect:/exchangeDetails/" + exchangeId;
    }

    @PostMapping(value = "/pickOffer")
    public String pickOffer(
            @RequestParam(name = "exchange_id") Long exchangeId,
            @RequestParam(name = "offer_id") Long offerId,
            RedirectAttributes redirAttrs) {

        Offers offer = this.offerService.getOffer(offerId);
        offer.setPicked(true);

        Exchanges exchange = this.exchangeService.getExchange(exchangeId);

        exchange.setStatus(ExchangeStatus.FREEZED);

        sendEmailService.sendEmail (offer.getUser().getEmail(), "Your offer has been picked, please visit our website to connect with user to continue the exchange http://localhost:8000/chat?"
                                            + "opponent="+getUserData().getEmail()+"&exchange_id="+exchangeId+"&offer_id="+offerId
                , "Your offer has been picked!!!");

        this.offerService.saveOffer(offer);
        this.exchangeService.saveExchange(exchange);

        this.exchangeWebRequest.setOfferBooks(new ArrayList<>());

        redirAttrs.addFlashAttribute("successA", "Offer successfully picked");

        return "redirect:/exchangeDetails/" + exchangeId;
    }

    @PostMapping(value = "/cancelOffer")
    public String cancelOffer(
            @RequestParam(name = "exchange_id") Long exchangeId,
            @RequestParam(name = "offer_id") Long offerId,
            RedirectAttributes redirAttrs) {

        Offers offer = this.offerService.getOffer(offerId);
        offer.setPicked(false);

        Exchanges exchange = this.exchangeService.getExchange(exchangeId);

        exchange.setStatus(ExchangeStatus.OPENED);
        exchange.setFirstConfirm(0L);
        exchange.setSecondConfirm(0L);

        this.offerService.saveOffer(offer);
        this.exchangeService.saveExchange(exchange);

        this.exchangeWebRequest.setOfferBooks(new ArrayList<>());

        redirAttrs.addFlashAttribute("successA", "Offer successfully canceled");

        return "redirect:/profile";
    }

    @PostMapping(value = "/confirmExchange")
    public String confirmExchange(
            @RequestParam(name = "exchange_id") Long exchangeId,
            @RequestParam(name = "offer_id") Long offerId,
            RedirectAttributes redirAttrs) {

        Exchanges exchange = this.exchangeService.getExchange(exchangeId);


        Offers offer = this.offerService.getOffer(offerId);

        if(offer.isPicked()) {
            if (exchange.getFirstConfirm() == 0L) {
                exchange.setFirstConfirm(getUserData().getId());
                String email = "";
                String opEmail = "";
                if(offer.getUser().getEmail().equals(getUserData().getEmail())){
                    email = exchange.getUser().getEmail();
                    opEmail = offer.getUser().getEmail();
                } else {
                    email = offer.getUser().getEmail();
                    opEmail = exchange.getUser().getEmail();
                }
                sendEmailService.sendEmail (email, "Your opponent confirmed exchange, please confirm it too or cancel if exchange didn't finished http://localhost:8000/chat?"
            + "opponent="+opEmail+"&exchange_id="+exchangeId+"&offer_id="+offerId
                        , "Your opponent confirmed exchange!!!");

            } else {
                exchange.setSecondConfirm(getUserData().getId());
                exchange.setStatus(ExchangeStatus.CLOSED);
            }


            this.exchangeService.saveExchange(exchange);
        }

        redirAttrs.addFlashAttribute("successA", "Exchange successfully confirmed");

        return "redirect:/profile";
    }

    @GetMapping(value = "/createExchange")
    public String createExchange(Model model, @RequestParam(name="searchStr",required = false) String searchStr,
                                @RequestParam(name="searchUserStr",required = false) String searchUserStr
                                 ) {

        List<UserBooks> userBooks = new ArrayList<>();

        if (searchUserStr == null || searchUserStr.equals("")){
            userBooks = this.userBookService.getAllBooksByUser(getUserData());
        }
        if (searchUserStr != null){
            userBooks = this.userBookService.getAllUserBooksSearch(searchUserStr, getUserData());
        }

        List<Books> books = new ArrayList<>();

        if (searchStr == null || searchStr.equals("")){
            books = this.bookService.getAllBooks();
        }
        if (searchStr != null){
            books = this.bookService.getAllBooksSearch(searchStr);
        }




        List<Books> currentBooks = new ArrayList<>();
        List<UserBooks> currentUserBooks = new ArrayList<>();


        for (Long id:this.exchangeWebRequest.getUserBooks()) {
            UserBooks userBook = this.userBookService.getBook(id);
            currentUserBooks.add(userBook);
            userBooks.remove(userBook);
        }

        for (Long id:this.exchangeWebRequest.getBooks()) {
            Books book = this.bookService.getBook(id);
            currentBooks.add(book);
            books.remove(book);
        }



        model.addAttribute("userBooks", userBooks);
        model.addAttribute("books", books);

        model.addAttribute("currentBooks", currentBooks);
        model.addAttribute("currentUserBooks", currentUserBooks);


        model.addAttribute("currentUser", getUserData());

        return "createExchange";
    }

    @PostMapping(value = "/addbooktooffer")
    public String addbooktooffer(
            @RequestParam(name = "id") Long id,
            @RequestParam(name = "type") String type,
            @RequestParam(name = "exchange_id") Long exchangeId,
            RedirectAttributes redirAttrs
            ) {

        if (type.equals("offerBook")){
            if(exchangeWebRequest.getOfferBooks().size() != 6) {
                this.exchangeWebRequest.addOfferBook(id);
            } else {
                redirAttrs.addFlashAttribute("errorA", "The number of books must not exceed 6");
            }
        }

        return "redirect:/exchangeDetails/" + exchangeId + "#pickUserBooks";
    }

    @GetMapping(value = "/removebookfromoffer")
    public String removebookfromoffer(
            @RequestParam(name = "id") Long id,
            @RequestParam(name = "exchange_id") Long exchangeId) {

            this.exchangeWebRequest.getOfferBooks().remove(id);

        return "redirect:/exchangeDetails/" + exchangeId + "#pickUserBooks";
    }

    @PostMapping(value = "/addbooktoexchange")
    public String addBookToExchange(
            @RequestParam(name = "id") Long id,
            @RequestParam(name = "type") String type,
            RedirectAttributes redirAttrs) {
        String redir ="";
        if (type.equals("userBook")){
            if(exchangeWebRequest.getUserBooks().size() != 6){
            this.exchangeWebRequest.addUserBook(id);
                redir = "#pickUserBooks";
            }
            else {
                redirAttrs.addFlashAttribute("errorA", "The number of books must not exceed 6");
            }

        }

        if (type.equals("book")){
            if(exchangeWebRequest.getBooks().size() != 6){
            this.exchangeWebRequest.addBook(id);
                redir = "#pickBooks";
            }
            else {
                redirAttrs.addFlashAttribute("errorA", "The number of books must not exceed 6");
            }

        }

        return "redirect:/createExchange" + redir;
    }


    @GetMapping(value = "/removebookfromexchange")
    public String removeBookFromExchange(
            @RequestParam(name = "id") Long id,
            @RequestParam(name = "type") String type) {

        if (type.equals("userBook")){
            this.exchangeWebRequest.getUserBooks().remove(id);

        }

        if (type.equals("book")){
            this.exchangeWebRequest.getBooks().remove(id);
        }




        return "redirect:/createExchange";
    }

    @PostMapping(value = "/submitExchange")
    public String submitExchange(
            @RequestParam(name = "comment") String comment,  RedirectAttributes redirAttrs) {

        if (this.exchangeWebRequest.getUserBooks().isEmpty()){
            redirAttrs.addFlashAttribute("errorA", "Pick your books please");
            return "redirect:/createExchange";
        }
        if (this.exchangeWebRequest.getBooks().isEmpty()){
            redirAttrs.addFlashAttribute("errorA", "Pick books that you want please");
            return "redirect:/createExchange";
        }
        Exchanges exchange = new Exchanges();

        List<Books> currentBooks = new ArrayList<>();
        List<UserBooks> currentUserBooks = new ArrayList<>();


        for (Long id:this.exchangeWebRequest.getUserBooks()) {
            UserBooks userBook = this.userBookService.getBook(id);
            currentUserBooks.add(userBook);
        }
        this.exchangeWebRequest.setUserBooks(new ArrayList<>());

        for (Long id:this.exchangeWebRequest.getBooks()) {
            Books book = this.bookService.getBook(id);
            currentBooks.add(book);
        }
        this.exchangeWebRequest.setBooks(new ArrayList<>());


        exchange.setBooks(currentBooks);
        exchange.setUserBooks(currentUserBooks);
        exchange.setComment(comment);
        exchange.setUser(getUserData());
        exchange.setDate(new Date());

        exchangeService.addExchange(exchange);



        redirAttrs.addFlashAttribute("successA", "Exchange successfully created");

        return "redirect:/";
    }

    @GetMapping(value = "/viewbookex/{url}", produces = {MediaType.IMAGE_JPEG_VALUE})
    public @ResponseBody
    byte[] viewBookPhoto(@PathVariable(name = "url") String url) throws IOException {

        String pictureURL = "";
        if (url != null) {
            pictureURL = viewPath + url + ".jpg";
        }
        InputStream in;
        try {
            ClassPathResource resource = new ClassPathResource(pictureURL);
            in = resource.getInputStream();
        } catch (Exception e) {
            ClassPathResource resource = new ClassPathResource(viewPath);
            in = resource.getInputStream();
            e.printStackTrace();
        }

        return org.apache.commons.io.IOUtils.toByteArray(in);
    }

    private Users getUserData() {
        Authentication authontication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authontication instanceof AnonymousAuthenticationToken)) {
            User secUser = (User) authontication.getPrincipal();
            Users myUser = userService.getUserByEmail(secUser.getUsername());
            return myUser;
        }
        return null;
    }
}
