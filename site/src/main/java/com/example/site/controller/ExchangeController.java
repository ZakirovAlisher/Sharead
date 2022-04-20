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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class ExchangeController {

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
    public String exchangeDetails(Model m ,@PathVariable(name = "id") Long id){

        Exchanges exchange = this.exchangeService.getExchange(id);

        m.addAttribute("exchange", exchange);
        m.addAttribute("offers", this.offerService.getByExchange(exchange));


        return "exchangeDetails";
    }

    @PostMapping(value = "/postOffer")
    public String addOfferToExchange(
            @RequestParam(name = "exchange_id") Long exchangeId,
            @RequestParam(name = "comment") String comment) {

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

        return "redirect:/exchangeDetails" + exchangeId;
    }

    @PostMapping(value = "/pickOffer")
    public String pickOffer(
            @RequestParam(name = "exchange_id") Long exchangeId,
            @RequestParam(name = "offer_id") Long offerId) {

        Offers offer = this.offerService.getOffer(offerId);
        offer.setPicked(true);

        Exchanges exchange = this.exchangeService.getExchange(exchangeId);

        exchange.setStatus(ExchangeStatus.FREEZED);

        //todo: отправка эмеила оферисту
        //todo: в профиле чтоб можно было связаться в чате



        this.offerService.saveOffer(offer);
        this.exchangeService.saveExchange(exchange);

        this.exchangeWebRequest.setOfferBooks(new ArrayList<>());

        return "redirect:/exchangeDetails" + exchangeId;
    }

    @GetMapping(value = "/createExchange")
    public String createExchange(Model model) {

        List<UserBooks> userBooks = this.userBookService.getAllBooksByUser(getUserData());

        List<Books> books = this.bookService.getAllBooks();



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

//        for (Long id:this.exchangeWebRequest.getAuthors()) {
//            Authors author = this.authorService.getAuthor(id);
//            currentAuthors.add(author);
//            authors.remove(author);
//        }
//
//        for (Long id:this.exchangeWebRequest.getGenres()) {
//            Genres genre = this.genreService.getGenre(id);
//            currentGenres.add(genre);
//            genres.remove(genre);
//        }

        model.addAttribute("userBooks", userBooks);
        model.addAttribute("books", books);
//        model.addAttribute("authors", authors);
//        model.addAttribute("genres", genres);

        model.addAttribute("currentBooks", currentBooks);
        model.addAttribute("currentUserBooks", currentUserBooks);
//        model.addAttribute("currentAuthors", currentAuthors);
//        model.addAttribute("currentGenres", currentGenres);


        model.addAttribute("currentUser", getUserData());

        return "createExchange";
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
        }

//        if (type.equals("genre")){
//            this.exchangeWebRequest.addGenre(id);
//        }
//
//        if (type.equals("author")){
//            this.exchangeWebRequest.addAuthor(id);
//        }

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

//        if (type.equals("genre")){
//            this.exchangeWebRequest.getGenres().remove(id);
//        }
//
//        if (type.equals("author")){
//            this.exchangeWebRequest.getAuthors().remove(id);
//        }

        return "redirect:/createExchange";
    }

    @PostMapping(value = "/submitExchange")
    public String submitExchange(
            @RequestParam(name = "comment") String comment) {

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
