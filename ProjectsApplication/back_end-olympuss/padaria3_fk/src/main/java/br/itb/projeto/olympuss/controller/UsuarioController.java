package br.itb.projeto.olympuss.controller;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import br.itb.projeto.olympuss.model.entity.Usuario;
import br.itb.projeto.olympuss.service.PlanoService;
import br.itb.projeto.olympuss.service.UsuarioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

	private UsuarioService usuarioService;
	
	private PlanoService planoService;

	public UsuarioController(UsuarioService usuarioService, PlanoService planoService) {
		super();
		this.usuarioService = usuarioService;
		this.planoService = planoService;
	}

	private String serverMessage = null;
	private String foto = "";
	// CASO O PRODUTO NÃO TENHA UMA IMAGEM CADASTRADA NO BANCO DE DADOS
	private String noImage = "/images/perfil.png";
	
	

	@GetMapping("crud")
	public String crud(ModelMap model) {
		
		model.addAttribute("planos", planoService.findAll()); 
		model.addAttribute("usuarios", usuarioService.findAll()); 
		model.addAttribute("usuario", new Usuario()); 
		
		return "crud";
	}
	

	@GetMapping("/login")
	public String getLogin(ModelMap model, HttpSession session) {
		
		model.addAttribute("usuario", new Usuario());
		model.addAttribute("serverMessage", serverMessage);
		serverMessage = "";
		return "logne2";
	}
	@GetMapping("/listacliente")
	public String listacliente(ModelMap model) {
	
		model.addAttribute("usuarios", usuarioService.findAll()); 
		model.addAttribute("planos", planoService.findAll());
		model.addAttribute("usuario", new Usuario()); 
		return "listacliente";
	}
	
	@GetMapping("/ver/{id}")

	public String verUsuario(@PathVariable("id") long id, ModelMap model) {

 

		Usuario usuario = usuarioService.findById(id);

 
		model.addAttribute("planos", planoService.findAll()); 
		model.addAttribute("usuario", usuario);

 

		model.addAttribute("serverMessage", serverMessage);

		serverMessage = "";

		return "editar_usuario";

	}
	
	@PostMapping("/usuario/editar/{id}")

	public String editar(

			@PathVariable("id") long id,

			@ModelAttribute("usuario") Usuario usuario) {

 

		usuarioService.update(usuario);

		serverMessage = "Dados alterados com sucesso!!!";

		return "redirect:/usuario/listacliente";

	}
	
	@GetMapping("/novo-login")
	public String getNovoLogin(ModelMap model) {

		model.addAttribute("usuario", new Usuario());
		model.addAttribute("serverMessage", serverMessage);
		serverMessage = "";
		return "novo-login";
	}
	
	@GetMapping("/usuario-novo")
	public String getNovoUsuario(ModelMap model) {

		model.addAttribute("usuario", new Usuario());
		model.addAttribute("serverMessage", serverMessage);

		serverMessage = null;
		
		return "usuario-novo";
	}
	
	
	@GetMapping("/voltar")
	public String getHome(ModelMap model, HttpSession session) {
		//System.out.println(session.getAttribute("usuarioLogado"));
		serverMessage = null;
		return "redirect:/api/inicio";
	}
	
	@PostMapping("/acessar")
	public String acessar(ModelMap model,
			@RequestParam("EMAIL") String email, 
			@RequestParam("SENHA") String senha, HttpSession session) {
		
		Usuario usuarioLogado = usuarioService.acessar(email, senha);
		
		if (usuarioLogado != null) {
						
			session.setAttribute("usuarioLogado", usuarioLogado);
			model.addAttribute("usuario", usuarioLogado);
			
			if (usuarioLogado.getNIVEL_ACESSO().equals("ADMIN")) {
				
				return "redirect:/usuario/admin-inicio";
				
			} else if (usuarioLogado.getNIVEL_ACESSO().equals("TEC")) {
				
				return "redirect:/usuario/admin-inicio";
				
			} else if (usuarioLogado.getNIVEL_ACESSO().equals("USER")) {
				
				return "redirect:/usuario/admin-inicio";
				
			}
	
		}
		
		serverMessage = "Dados Incorretos!";
		model.addAttribute("serverMessage", serverMessage);
		
		return "redirect:/usuario/login";
	}
	
	

	@GetMapping("/admin-inicio")
	public String goAdmin(ModelMap model, HttpSession session) {

		Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
		model.addAttribute("usuario", session.getAttribute("usuarioLogado"));
			

		model.addAttribute("noImage", noImage);
		model.addAttribute("serverMessage", serverMessage);

		return "inicio";
	}
	
	@GetMapping("/user")
	public String goUser(ModelMap model, HttpSession session) {

		Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
		model.addAttribute("usuario", session.getAttribute("usuarioLogado"));
		model.addAttribute("noImage", noImage);
		model.addAttribute("serverMessage", serverMessage);

		return "user";
	}
	
	@GetMapping("/tec")
	public String goTec(ModelMap model, HttpSession session) {

		Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
		model.addAttribute("usuario", session.getAttribute("usuarioLogado"));
		model.addAttribute("noImage", noImage);
		model.addAttribute("serverMessage", serverMessage);

		return "tec";
	}


	@GetMapping("/logoff")
	public String sair(ModelMap model, HttpSession session) {
		
		session.removeAttribute("usuarioLogado");
		
		serverMessage = "Usuário desconectado!";
		model.addAttribute("serverMessage", serverMessage);
		serverMessage = "";
		return "redirect:/api/index";
	}
	
	@PostMapping("/logoff")
	public String logoff(ModelMap model, HttpSession session) {

		session.removeAttribute("usuarioLogado");
		serverMessage = "";
		return "redirect:/api/index";
	}
	
	@PostMapping("/salvar")
	public String salvar(ModelMap model,
			@ModelAttribute("usuario") Usuario usuario) {
		
		Usuario _usuario = usuarioService.findByEmail(usuario.getEMAIL());
		
		if (_usuario != null) {
			
			usuarioService.save(usuario);
			model.addAttribute("usuario", new Usuario());
			serverMessage = "Usuário cadastrado com sucesso!!!";
			
		} else if (_usuario == null) {
			
			model.addAttribute("usuario", new Usuario());
			serverMessage = "Dados Incorretos!";	
			
		}
		
		if (usuario.getNOME_COMPLETO().equals("") || usuario.getEMAIL().equals("") || usuario.getSENHA().equals("")) {
			
			serverMessage = "Dados Incompletos!!!";	
			
		} 
	
		return "redirect:/usuario/listacliente";
	}
	
	@PostMapping("/gravar")
	public String gravar(ModelMap model,
			@ModelAttribute("usuario") Usuario usuario) {
		
		Usuario _usuario = usuarioService.findByEmail(usuario.getEMAIL());
		
		if (_usuario == null) {
			
			usuarioService.saveNewUser(usuario);
			model.addAttribute("usuario", new Usuario());
			serverMessage = "Usuário cadastrado com sucesso!!!";
			
		} else if (_usuario != null) {
			
			model.addAttribute("usuario", new Usuario());
			serverMessage = "Usuário já foi cadastrado no sitema!";	
			
		}
		
		if (usuario.getNOME_COMPLETO().equals("") || usuario.getEMAIL().equals("")) {
			
			serverMessage = "Dados Incompletos!!!";	
			
		} 
	
		return "redirect:/usuario/listacliente";
	}
	
	@PostMapping("/inativar/{id}")
	public String inativar (
			@PathVariable("id") long id, @ModelAttribute("usuario") Usuario usuario) {
	
		usuarioService.update(usuario);
		
		serverMessage = "Usuário inativado com sucesso!!!";
		foto = "";
		return "redirect:/api/usuario/ver/" + id;
	}
	
	@PostMapping("/alterarsenha")
	public String alterarsenha(
			@ModelAttribute("usuario") Usuario usuario, 
			@RequestParam("senha") String senha) {
		
		if (!senha.equals("")) {
			usuarioService.alterarsenha(usuario.getEMAIL(), senha);
			serverMessage = "Acesse o sistema com a nova senha.";
			return "redirect:/api/usuario/login";
		}
		serverMessage = "";
		serverMessage = "Informe a nova senha.";
		return "redirect:/api/usuario/novaSenha/"+usuario.getId();
	}
	
	
//	@GetMapping("/ver/{id}")
//	public String verUsuario(@PathVariable("id") long id, ModelMap model) {

//		Usuario usuario = usuarioService.findById(id);

//		model.addAttribute("usuario", usuario);
//		model.addAttribute("noImage", noImage);
//		model.addAttribute("serverMessage", serverMessage);
//		serverMessage = "";
//		return "usuario-editar";
//	}
	
	@PostMapping("/editar/{id}")
	public String editarsemFoto(@PathVariable("id") long id, @ModelAttribute("usuario") Usuario usuario) {

			
		usuarioService.updateSemFoto(usuario);
		
		serverMessage = "Dados alterados com sucesso!!!";

		return "redirect:/usuario/listacliente";
	}
	
	
//	@PostMapping("/editar/{id}")
//	public String editar(
//			@RequestParam(value = "file", required = false) MultipartFile file,
//			@PathVariable("id") long id, @ModelAttribute("usuario") Usuario usuario) {
//
//		byte[] _foto = Base64.getDecoder().decode(foto);
//			
//		usuarioService.update(file, usuario, _foto);
//		
//		serverMessage = "Dados alterados com sucesso!!!";
//		foto = "";
//		return "redirect:/api/usuario/verPerfil/" + id;
//	}
	
	@GetMapping("/verPerfil/{id}")
	public String verUsuarioPerfil(@PathVariable("id") long id, ModelMap model) {

		Usuario usuario = usuarioService.findById(id);
		
		if (usuario.getFOTO() != null) {
			if (usuario.getFOTO().length > 0) {
				foto = Base64.getEncoder().encodeToString(usuario.getFOTO());
			}
		}

		model.addAttribute("usuario", usuario);
		model.addAttribute("noImage", noImage);
		model.addAttribute("serverMessage", serverMessage);
		
		return "usuario-editarPerfil";
	}
	
	@GetMapping("/novaSenha/{id}")
	public String novaSenha(@PathVariable("id") long id, ModelMap model) {

		Usuario usuario = usuarioService.findById(id);

		model.addAttribute("usuario", usuario);
		model.addAttribute("serverMessage", serverMessage);

		return "usuario-novaSenha";
	}
	
	@GetMapping("/todos")
	public String verTodosUsuarios(ModelMap model) {

		List<Usuario> usuarios = usuarioService.findAll();

		model.addAttribute("usuarios", usuarios);
		model.addAttribute("serverMessage", serverMessage);

		return "usuarios";
	}
	
	@GetMapping("/showImage/{id}")
	@ResponseBody
	public void showImage(
			@PathVariable("id") long id, HttpServletResponse response, Usuario usuario)
			throws ServletException, IOException {

		usuario = usuarioService.findById(id);

		response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
		if (usuario.getFOTO() != null) {
			response.getOutputStream().write(usuario.getFOTO());
		} else {
			response.getOutputStream().write(null);
		}

		response.getOutputStream().close();
	}
	
	
}
