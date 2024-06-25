//担当者:菊水

package com.example.demo.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.User;
import com.example.demo.model.UserAccount;
import com.example.demo.repository.UserRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Controller
@RequestMapping("/user")
public class UserAccountController {

	@Autowired
	private HttpSession session;

	@Autowired
	UserRepository userRepository;

	@Autowired
	UserAccount userAccount;

	@GetMapping({ "/index", "/logout" })
	public String index() {
		//セッション破棄、強制返却処理AOP？
		session.invalidate();

		System.out.println("START");
		System.out.println("Uid:"+userAccount.getId());
		System.out.println("Uname:"+userAccount.getName());

		return "userLogin";//G101利用者ログイン画面へ
	}

	@RequestMapping(value = "/login", method = { RequestMethod.GET, RequestMethod.POST })
	public String login(
			@RequestParam(name = "email", required = false) String email,
			@RequestParam(name = "password", required = false) String password,
			//菊：エラー文章表示用Model
			Model m) {

		User user = null;

		//エラー処理あり
		//菊：空欄エラー
		if ((email == null || email.equals("")) || (password == null || password.equals(""))) {
			m.addAttribute("error", "全項目を入力してください");
			return "userLogin";
		}

		//菊：不一致エラー
		Optional<User> record = userRepository.findByEmailAndPassword(email, password);

		
		if (record.isPresent()) {
			//正常時
			//UserAccountクラスに名前をセット(セッション管理)
			user = record.get();
			userAccount.setName(user.getName());
			userAccount.setId(user.getId());
			
			System.out.println("Uid:"+userAccount.getId());
			System.out.println("Uname:"+userAccount.getName());
			
		} else {
			m.addAttribute("error", "メールアドレスまたはパスワードが一致していません");
			return "userLogin";
		}

		return "redirect:/show";//BookController 商品一覧へリダイレクト
	}

	@GetMapping("/add/form")
	public String addForm() {//完成！！！
		
		return "addUser";//G111利用者登録画面へ
	}

	@RequestMapping(value = "/add/check", method = { RequestMethod.GET, RequestMethod.POST })
	public String addCheck(
			@RequestParam(name = "name", required = false) String name,
			@RequestParam(name = "email", required = false) String email,
			@RequestParam(name = "password", required = false) String password,
			//name = というキーワードが、html上に存在しない場合に「null」
			Model m
	//みたいな感じ^^
	) {

		//エラー処理あり
		//菊：空欄エラー
		if ((name == null || name.equals("")) || (email == null || email.equals(""))
				|| (password == null || password.equals(""))) {
			m.addAttribute("error", "全項目を入力してください");
			return "addUser";
		}

		//		System.out.println("name:" + name);

		//菊：不一致エラー
		Optional<User> record = userRepository.findByEmail(email);

		if (record.isEmpty()) {
			//正常時
			m.addAttribute("name", name);
			//同様にメアド、パスワードを保持
			m.addAttribute("email", email);
			m.addAttribute("password", password);
		} else {
			m.addAttribute("error", "このメールアドレスは既に登録されています");
			return "addUser";
		}

		return "checkAddUser";//G112内容確認画面へ
	}
	
	@Transactional
	@RequestMapping(value = "/add/done", method = { RequestMethod.GET, RequestMethod.POST })
	public String addUser(
			@RequestParam(name = "name", required = false) String name,
			@RequestParam(name = "email", required = false) String email,
			@RequestParam(name = "password", required = false) String password,
			Model m
	//みたいな感じ^^
	) {
		//エラー処理なし

		//正常時
		//Userのインスタンス作成(名前、email、パスワード)
		User user = new User(name, email, password);

		//		System.out.println("id:" + user.getId());

		user.setCreatedBy(1);

		//UserRepository.saveAndFlushで作成したインスタンスを保存
		userRepository.saveAndFlush(user);

		//新しく作成したuserのidをゲッターで取得
		//｛注｝
		Integer id = user.getId();

		user.setCreatedBy(id);
		userRepository.saveAndFlush(user);

		m.addAttribute("name", name);
		//同様にメアド、idを保持
		m.addAttribute("email", email);
		m.addAttribute("id", id);

		return "doneAddUser";//登録完了画面へ
	}

}
