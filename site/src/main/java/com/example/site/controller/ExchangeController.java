package com.example.site.controller;

import com.example.site.domain.Authors;
import com.example.site.domain.Books;
import com.example.site.domain.Exchanges;
import com.example.site.domain.Genres;
import com.example.site.domain.Offers;
import com.example.site.domain.UserBooks;
import com.example.site.domain.Users;
import com.example.site.service.AuthorService;
import com.example.site.service.BookService;
import com.example.site.service.ExchangeService;
import com.example.site.service.GenreService;
import com.example.site.service.OfferService;
import com.example.site.service.SendEmailService;
import com.example.site.service.UserBookService;
import com.example.site.service.UserService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    @Value("${file.book.viewPath}")
    private String viewPath;

    @Autowired
    private ExchangeService exchangeService;

    @Autowired
    private OfferService offerService;

    @GetMapping(value = "/")
    public String exchanges(Model model){
        model.addAttribute("currentUser", getUserData());

        model.addAttribute("exchangesOpen",
                           this.exchangeService.getExchangesByStatus(ExchangeStatus.OPENED));
        model.addAttribute("exchangesFreeze",
                           this.exchangeService.getExchangesByStatus(ExchangeStatus.FREEZED));

        return "exchanges";
    }

    @GetMapping(value = "/exchangeDetails/{id}")
    public String exchangeDetails(Model m ,@PathVariable(name = "id") Long id,
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
            @RequestParam(name = "exchange_id") Long exchangeId
            ) {

        if (type.equals("offerBook")){
            if(exchangeWebRequest.getOfferBooks().size() != 6)
                this.exchangeWebRequest.addOfferBook(id);
        }

        return "redirect:/exchangeDetails/" + exchangeId;
    }

    @GetMapping(value = "/removebookfromoffer")
    public String removebookfromoffer(
            @RequestParam(name = "id") Long id,
            @RequestParam(name = "exchange_id") Long exchangeId) {

            this.exchangeWebRequest.getOfferBooks().remove(id);

        return "redirect:/exchangeDetails/" + exchangeId;
    }

    @PostMapping(value = "/addbooktoexchange")
    public String addBookToExchange(
            @RequestParam(name = "id") Long id,
            @RequestParam(name = "type") String type) {

        if (type.equals("userBook")){
            if(exchangeWebRequest.getUserBooks().size() != 6)
            this.exchangeWebRequest.addUserBook(id);
        }

        if (type.equals("book")){
            if(exchangeWebRequest.getBooks().size() != 6)
            this.exchangeWebRequest.addBook(id);
        }

        if (type.equals("offerBook")){
            if(exchangeWebRequest.getOfferBooks().size() != 6)
                this.exchangeWebRequest.addOfferBook(id);
            return "redirect:/createExchange";
        }


        return "redirect:/createExchange";
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

        if (type.equals("offerBook")){
            this.exchangeWebRequest.getOfferBooks().remove(id);
        }


        return "redirect:/createExchange";
    }

    @PostMapping(value = "/submitExchange")
    public String submitExchange(
            @RequestParam(name = "comment") String comment,  RedirectAttributes redirAttrs) {

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
