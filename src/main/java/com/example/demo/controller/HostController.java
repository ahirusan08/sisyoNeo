//担当：さきちゃん 
package com.example.demo.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.Book;
import com.example.demo.entity.Rental;
import com.example.demo.entity.User;
import com.example.demo.model.HostAccount;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.RentalRepository;
import com.example.demo.repository.UserRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Controller
@RequestMapping("/host")
public class HostController {

	@Autowired
	UserRepository userRepository;

	@Autowired
	BookRepository bookRepository;

	@Autowired
	RentalRepository rentalRepository;

	@Autowired
	HttpSession session;

	@Autowired
	HostAccount account;

	LocalDateTime nowDate = LocalDateTime.now();

	@GetMapping("/select")
	public String select() {
		
		session.removeAttribute("book");
		session.removeAttribute("userId");
		session.removeAttribute("name");
		System.out.println("userId:"+session.getAttribute("userId"));
		
		
		return "select";//G202機能選択画面
	}

	@GetMapping("/rental")
	public String rentalSearch() {
		//本ID、利用者ID入力画面表示

		return "rentalSearch";//G211貸出返却
	}

	@PostMapping("/rental/select")
	public String rentalSelect(
			//リクエストパラメータ
			Model model,
			@RequestParam(name = "bookId", required = false) Integer bookId,
			@RequestParam(name = "userId", required = false) Integer userId) {

		List<String> error = new ArrayList<>();

		//空白処理
		if (bookId == null || userId == null) {
			error.add("全項目を入力してください");
			model.addAttribute("error", error);
			return "rentalSearch";
		}
		
		//本ID、利用者IDをもとにタイトル、利用者名を検索
		Optional<Book> bookrecord = bookRepository.findById(bookId);
		Optional<User> userrecord = userRepository.findById(userId);

		Book book = null;
		User user = null;

		if (bookrecord.isEmpty() == false) {
			book = bookrecord.get();
		} else {
			error.add("入力した本IDは登録されていません。");
		}

		if (userrecord.isEmpty() == false) {
			user = userrecord.get();
		} else {
			error.add("入力した利用者IDは登録されていません。");
		}

		if (error.size() != 0) {
			model.addAttribute("error", error);
			return "rentalSearch";
		}

		//セッションスコープにsetAttributeで保存
		session.setAttribute("book", book);
		session.setAttribute("userId", userId);
		session.setAttribute("name", user.getName());
		
		System.out.println("userId:"+session.getAttribute("userId"));

		//↓貸出返却の選択へ
		return "rentalSelect";//G212貸出・返却選択
	}

	@GetMapping("/rental/process")
	public String rentalProcess(
			Model model,
			@RequestParam("rental") Integer rental) {
		if (rental == 1) {
			return "redirect:/host/rental/lend";
		} else {
			return "redirect:/host/rental/return";
		} //G212貸出・返却選択
	}

	@Transactional
	@GetMapping("/rental/lend")
	public String lendBook(Model model) {

		Book book = (Book) session.getAttribute("book");
		Integer userId = (Integer) session.getAttribute("userId");

		Optional<Rental> record = rentalRepository.findByBookIdAndReturnDateIsNull(book.getId());
		if (record.isPresent()) {
			String error = "この本は既に貸出されています";
			model.addAttribute("error", error);
			return "rentalSelect";
		}
		//貸出処理
		Rental rentalrecord = rentalRepository.saveAndFlush(new Rental(userId, book.getId(), account.getId()));

		if (rentalrecord == null) {

			return "rentalSelect";
		}
		LocalDateTime time = rentalrecord.getLimitDate();

		model.addAttribute("limitDate", time.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
		return "lendBook";//G213貸出完了
	}

	@Transactional
	@GetMapping("/rental/return")
	public String returnBook(Model model) {
		//返却処理
		Book book = (Book) session.getAttribute("book");
		Integer userId = (Integer) session.getAttribute("userId");

		//返却処理
		Optional<Rental> rentalrecord = rentalRepository.findByBookIdAndUserIdAndReturnDateIsNull(book.getId(), userId);

		if (rentalrecord.isPresent()) {
			Rental rent = rentalrecord.get();
			rent.update(account.getId());
			rentalRepository.saveAndFlush(rent);
		} else {
			Optional<Rental> bookrecord = rentalRepository.findByBookIdAndReturnDateIsNull(book.getId());
			if (bookrecord.isPresent()) {
				String error = "この本は別の利用者によって貸出されています";
				model.addAttribute("error", error);
				return "rentalSelect";

			} else {
				String error = "この本は貸出されていません";
				model.addAttribute("error", error);
				return "rentalSelect";
			}
		}

		return "returnBook";//G214返却完了
	}

	@GetMapping("/add/book")
	public String addBook() {
		//
		return "addBook";//G221
	}

	@Transactional
	@PostMapping("/add/book/done")
	public String addBookDone(Model model,
			@RequestParam(name = "title", required = false) String title,
			@RequestParam(name = "author", required = false) String author) {

		if (title.equals("") || author.equals("")) {
			String error = "全項目を入力してください";
			model.addAttribute("error", error);
			return "addBook";
		}

		Book book = new Book(title, author, account.getId());
		bookRepository.save(book);

		model.addAttribute("title", title);
		model.addAttribute("author", author);

		return "doneAddBook";//G222
	}
	
	
	@GetMapping("/rentallook")
	public String rental() {
		
			return "lookSearch";
	}

	@PostMapping("/look/select")
	public String searchlook(
			Model model,
			@RequestParam(name = "bookId", required = false) Integer bookId,
			@RequestParam(name = "userId", required = false) Integer userId			
			) {
		
		List<String> error = new ArrayList<>();

		//空白処理
		if (bookId == null || userId == null) {
			error.add("全項目を入力してください");
			model.addAttribute("error", error);
			return "lookSearch";
		}
		
		
		//本ID、利用者IDをもとにタイトル、利用者名を検索
		Optional<Book> bookrecord = bookRepository.findById(bookId);
		Optional<User> userrecord = userRepository.findById(userId);

		if (bookrecord.isEmpty() == true) {
			error.add("入力した本IDは登録されていません。");
		}

		if (userrecord.isEmpty() == true) {
			error.add("入力した利用者IDは登録されていません。");
		}

		if (error.size() != 0) {
			model.addAttribute("error", error);
			return "lookSearch";
		}

		
		Optional<Rental> rentalrecord = rentalRepository.findByBookIdAndUserIdAndReturnDateIsNull(bookId, userId);
		
		if(rentalrecord.isPresent()) {
			User user = userrecord.get();
			session.setAttribute("book",bookrecord.get());
			session.setAttribute("name",user.getName());
			Rental rental = rentalrecord.get();
			session.setAttribute("rental",rental);

			return "selectDate";
			
		}else {
		
		if(rentalrecord.isEmpty() == true) {
			
			Optional<Rental> book2record = rentalRepository.findByBookIdAndReturnDateIsNull(bookrecord.get().getId());
			if (book2record.isPresent()) {
				error.add("この本は別の利用者によって貸出されています");
				model.addAttribute("error", error);
				return "lookSearch";

			} else {
				
				 error.add("この本は貸出されていません");
				model.addAttribute("error", error);
				return "lookSearch";
			}
			
		}
		}
		
		
		
			return "lookSearch";
	}
	
	@PostMapping("/look/done")
	public String lookdone(
			Model model,
			@RequestParam(name = "date", required = false) LocalDate date
			) {
	
		if(date == null){
			String error = "日付を選択してください";
			model.addAttribute("error",error);
			return "selectDate";
		}
		Rental rental = (Rental) session.getAttribute("rental");
		
		LocalDateTime localDateTime = date.atStartOfDay();
		
		rental.update1(account.getId());
		rental.setLimitDate(localDateTime);
		rentalRepository.saveAndFlush(rental);
		
		model.addAttribute("limitDate",localDateTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
		
			return "lendBook";
	}

	
	//｛注｝
	@GetMapping("/rental/how")
	public String how(
			Model m
			) {
		
		List<Book> books = new ArrayList<>();
		
		List<User> users = new ArrayList<>();
		
		List<Rental> rentals = rentalRepository.findByReturnDateIsNullOrderByUserId();
//		m.addAttribute("rentals", rentals);
		
		List<String> starts = new ArrayList<>();
		List<String> ends = new ArrayList<>();
		
		
		Integer row = rentals.size();
		
		if (row == null || row == 0) {
			return "redirect:/host/select";
		}

		for(Rental r : rentals) {
			
			books.add(bookRepository.findById(r.getBookId()).get());
			
			users.add(userRepository.findById(r.getUserId()).get());
			
			LocalDateTime rd = r.getRentalDate();
			starts.add(rd.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
			
			LocalDateTime ld = r.getLimitDate();
			ends.add(ld.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
		}
		
		
		m.addAttribute("row", row);
		
		m.addAttribute("books", books);
		m.addAttribute("users", users);
		m.addAttribute("starts", starts);
		m.addAttribute("ends", ends);
		
		
		return "rentalHow";
	}


}
