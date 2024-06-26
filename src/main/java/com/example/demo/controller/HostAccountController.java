//tao
package com.example.demo.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.Host;
import com.example.demo.model.HostAccount;
import com.example.demo.repository.HostRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Controller
@RequestMapping("/host")
public class HostAccountController {

	@Autowired
	private HttpSession session;

	@Autowired
	private HostAccount haccount;

	@Autowired
	HostRepository hostrepository;

	@GetMapping({ "/index", "/logout" })
	public String index() {
		//セッション破棄、強制返却処理AOP？
		session.invalidate();
		System.out.println("Hid:" + haccount.getId());
		System.out.println("Hname:" + haccount.getName());

		return "hostLogin";
	}

	@PostMapping("/login")
	public String login(
			@RequestParam(name = "id", required = false) Integer id,
			@RequestParam(name = "password", required = false) String password,
			Model m) {

		if (id == null || password == null || password.equals("")) {
			m.addAttribute("error", "全項目を入力してください");
			return "hostLogin";
		}

		Host host = null;
		Optional<Host> record = hostrepository.findByIdAndPassword(id, password);

		if (record.isPresent()) {
			host = record.get();
		}

		if (host == null) {
			m.addAttribute("error", "管理者IDまたはパスワードが一致していません");
			return "hostLogin";
		}

		haccount.setName(host.getName());
		haccount.setId(host.getId());
		System.out.println("Hid:" + haccount.getId());
		System.out.println("Hname:" + haccount.getName());

		return "select";//G202

	}

	@GetMapping("/add/form")
	public String addForm() {//完成！！！

		return "addHost";//G111利用者登録画面へ
	}

	@RequestMapping(value = "/add/check", method = { RequestMethod.GET, RequestMethod.POST })
	public String addCheck(
			@RequestParam(name = "name", required = false) String name,
			@RequestParam(name = "password", required = false) String password,
			//name = というキーワードが、html上に存在しない場合に「null」
			Model m
	//みたいな感じ^^
	) {

		//エラー処理あり
		//菊：空欄エラー
		if ((name == null || name.equals("")) || (password == null || password.equals(""))) {
			m.addAttribute("error", "全項目を入力してください");
			return "addHost";
		}

		m.addAttribute("name", name);
		m.addAttribute("password", password);

		return "checkAddHost";//G112内容確認画面へ
	}

	@Transactional
	@RequestMapping(value = "/add/account", method = { RequestMethod.GET, RequestMethod.POST })
	public String addHost(
			@RequestParam(name = "name", required = false) String name,
			@RequestParam(name = "password", required = false) String password,
			Model m
	//みたいな感じ^^
	) {
		//エラー処理なし

		//正常時
		//Userのインスタンス作成(名前、email、パスワード)
		Host host = new Host(name, password);

		//		System.out.println("id:" + user.getId());

		host.setCreatedBy(1);

		//UserRepository.saveAndFlushで作成したインスタンスを保存
		hostrepository.saveAndFlush(host);

		//新しく作成したuserのidをゲッターで取得
		//｛注｝
		Integer id = host.getId();

		host.setCreatedBy(id);
		hostrepository.saveAndFlush(host);

		m.addAttribute("name", name);
		m.addAttribute("password", password);
		//同様にメアド、idを保持

		m.addAttribute("id", id);

		return "doneAddHost";//登録完了画面へ
	}

}
