package com.example.demo.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.Book;
import com.example.demo.entity.Rental;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.RentalRepository;

@Controller
public class BookController {

	//	@Autowired
	//	private HttpSession session;

	@Autowired
	BookRepository bookRepository;

	@Autowired
	RentalRepository rentalRepository;

	@RequestMapping("/show")
	public String show(
			@RequestParam(name = "title", required = false) String title,
			@RequestParam(name = "author", required = false) String author,
			Model m) {
		
		List<Book> books = null;
		if (title == null && author == null || title.equals("") && author.equals("")) {
			books = bookRepository.findAll();
		} else {
			books = bookRepository.findByTitleLikeAndAuthorLike("%" + title + "%", "%" + author + "%");
		}
		m.addAttribute("title", title);
		m.addAttribute("author", author);
		m.addAttribute("books", books);
		return "showBook";
	}

	@RequestMapping("/search")
	public String search(
			@RequestParam(name = "ym", required = false) String ym,
			@RequestParam(name = "title", required = false) String title,
			@RequestParam(name = "author", required = false) String author,
			Model m) {

		//検索データ保持
		m.addAttribute("ym", ym);
		m.addAttribute("title", title);
		m.addAttribute("author", author);

		//本検索
		List<Book> books = bookRepository.findAll();
		if ((title != null || author != null)) {
			books = bookRepository.findByTitleLikeAndAuthorLike("%" + title + "%", "%" + author + "%");
		}
		
		m.addAttribute("books", books);

		//年月ymの型変換
		LocalDate today = LocalDate.now();
		Integer year;
		Integer month;
		if (ym == null || ym.equals("")) {
			//検索データなしのときは今月
			year=today.getYear();
			month=today.getMonthValue();
			if( String.valueOf(month).length()==1) {//一桁の月は
				String a="0"+month;//06 のように表示
				m.addAttribute("ym", year+"-"+a);
			}else {
				m.addAttribute("ym", year+"-"+month);
			}
			
		}else {
			String yearMonth[] = ym.split("-");
			year = Integer.parseInt(yearMonth[0]);
			month = Integer.parseInt(yearMonth[1]);
			
		}

		m.addAttribute("year", year);
		m.addAttribute("month", month);

		//その月は何日まで？
		LocalDate date = LocalDate.of(year, month, 1);
		int maxDay = date.lengthOfMonth();
		m.addAttribute("maxDay", maxDay);

		//貸出状況表示
		//①本は全部で何冊？
		Integer allBooks = books.size();
		m.addAttribute("allBooks", allBooks);

		//②-1 貸出状況を管理する多次元配列bookRentalを作成
		Integer[][] rentalBook = new Integer[allBooks][31];

		//②-2 bookRental初期化
		for (int i = 0; i < allBooks; i++) {//すべての本の
			for (int j = 0; j < 31; j++) {//全日程において
				rentalBook[i][j] = 0;//貸出状況を返却状態(0)に初期化
			}
		}

		List<Rental> rentals = null;

		//③貸出中なら1をセット
		for (int i = 0; i < allBooks; i++) {//すべての本に対して
			rentals = rentalRepository.findByBookId(i + 1);//bookIdの一致するrentalsを検索

			for (Rental r : rentals) {//rentalsに対して
				for (int j = 0; j < 31; j++) {//すべての日程に対して

					//System.out.println("貸出日：" + r.getRentalDate());

					//③-0	 まだ返却されていない場合
					if (r.getReturnDate() == null) {
						r.setReturnDate(r.getLimitDate());//返却日に返却期限日を仮set
					}

					//③-1	 月をまたがない予約(貸出日と返却期限日が今月)
					//LocalDate today = LocalDate.now();
					if (r.getRentalDate().getMonthValue() == month && r.getReturnDate().getMonthValue() == month
							&& r.getRentalDate().getYear() == year && r.getReturnDate().getYear() == year) {

						//LocalDate today = LocalDate.now();
						//System.out.println("today:" + today);

						if (r.getRentalDate().getDayOfMonth() <= j + 1
								&& r.getReturnDate().getDayOfMonth() >= j + 1) {
						//	System.out.println(r.getRentalDate());
							rentalBook[i][j] = 1;

							if (
							//r.getRentalDate() == r.getReturnDate() //貸し出した日に返却された場合
							r.getRentalDate().isEqual(r.getReturnDate())
									&& r.getRentalDate().getDayOfMonth() == today.getDayOfMonth()//かつ 当日の貸出の場合

							) {

								rentalBook[i][j] = 0;

							}

						}

					}

					//③-2	 月をまたぐ予約
					//③-2-1 貸出日は今月だが返却日が来月のとき
					if (r.getRentalDate().getMonthValue() == month && r.getReturnDate().getMonthValue() == month + 1
							&& r.getRentalDate().getYear() == year && r.getReturnDate().getYear() == year) {
						if (r.getRentalDate().getDayOfMonth() <= j + 1) {
							rentalBook[i][j] = 1;
						}
					}
					//③-2-2 貸出日は先月だが返却日が今月のとき
					if (r.getRentalDate().getMonthValue() == month - 1 && r.getReturnDate().getMonthValue() == month
							&& r.getRentalDate().getYear() == year && r.getReturnDate().getYear() == year) {
						if (r.getReturnDate().getDayOfMonth() >= j + 1) {
							rentalBook[i][j] = 1;
						}
					}

					//③-3年をまたぐ予約
					//③-3-1 貸出日は今月だが返却日が来月のとき(12月末の貸出)
					if (r.getRentalDate().getMonthValue() == month && r.getReturnDate().getMonthValue() == month - 11
							&& r.getRentalDate().getYear() == year && r.getReturnDate().getYear() == year + 1) {
						if (r.getRentalDate().getDayOfMonth() <= j + 1) {
							rentalBook[i][j] = 1;
						}
					}

					//③-3-2 貸出日は先月だが返却日が今月のとき(1月初旬の返却)
					if (r.getRentalDate().getMonthValue() == month + 11 && r.getReturnDate().getMonthValue() == month
							&& r.getRentalDate().getYear() == year - 1 && r.getReturnDate().getYear() == year) {
						if (r.getReturnDate().getDayOfMonth() >= j + 1) {
							rentalBook[i][j] = 1;
						}
					}

				}
			}
		}

		//④データをHTMLへ
		m.addAttribute("rentalBook", rentalBook);

		return "searchBook";

	}

}
