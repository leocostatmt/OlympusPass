package br.itb.projeto.olympuss.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import br.itb.projeto.olympuss.model.entity.Plano;
import br.itb.projeto.olympuss.model.entity.Usuario;
import br.itb.projeto.olympuss.service.CheckinService;
import br.itb.projeto.olympuss.service.PlanoService;

@Controller		// DEFINE O ENDEREÇO NA URL PARA ACESSAR OS "END_POINTS" DA CLASSE
@RequestMapping("/plano")
public class PlanoController {

	private PlanoService planoservice;
	private CheckinService categoriaService;
		
	public PlanoController(PlanoService planoService, CheckinService categoriaService) {
		super();
		this.planoservice = planoService;
		this.categoriaService = categoriaService;
	}

	private String serverMessage = null;
	private String foto = "";
	// CASO O PRODUTO NÃO TENHA UMA IMAGEM CADASTRADA NO BANCO DE DADOS
	private String noImage = "/images/no-pizza-image.png";
	
	// LISTA TODOS OS PRODUTOS PARA O ADMIN
	// @GetMapping("/todos" --> END_POINT)
	// END_POINT -> EXECUTA UMA AÇÃO QUANDO É INFORMADO NA URL
	@GetMapping("/todos")
	public String verTodosPlanos(ModelMap model) {

		List<Plano> planos = PlanoService.findAll1();

		// DEFINE UM ATRIBUTO NA PÁGINA QUE IRÁ REPRESENTAR OS DADOS VINDOS DA APLICAÇÃO 
		model.addAttribute("planos", planos);
		serverMessage = null;

		// INDICA A PÁGINA QUE SERÁ CARREGADA NA EXECUÇÃO DO MÉTODO
		return "planos";
	}
	

	// LISTA TODOS OS PRODUTOS QUE SERÃO EXIBIDOS NA PÁGINA INICIAL
	@GetMapping("/lista")
	public String verListaProdutos(ModelMap model) {

		List<Plano> planos = planoservice.findAll();
		model.addAttribute("planos", planos);
		model.addAttribute("noImage", noImage);
		serverMessage = null;

		return "planos-lista";
	}
	
	// NAVEGA PARA PÁGINA PARA CADASTRO DE UM NOVO PRODUTO
	@GetMapping("/novo")
	public String novoProduto(ModelMap model) {

		model.addAttribute("plano", new Plano());
		model.addAttribute("categorias", categoriaService.findAll());
		model.addAttribute("serverMessage", serverMessage);
		serverMessage = null;
		return "plano-novo";
	}
	
	// GRAVA AS INFORMAÇÕES DO PRODUTO
	// MultipartFile file, NECESSÁRIO PARA GRAVAR A IMAGEM DO PRODUTO
	@PostMapping("/salvar")
	public String salvar(
			@RequestParam(value = "file", required = false) MultipartFile file,
			@ModelAttribute("plano") Plano plano) {
		
		planoservice.saveNew(plano);
		
		serverMessage = "Produto cadastrado com sucesso!!!";

		// REDIRECIONA PARA O END_POINT INDICADO
		return "redirect:/plano/cadastro" ;
	}
	
	
	// CARREGA AS INFORMAÇÕES DO PRODUTO
//	@GetMapping("/ver/{id}")
//	public String verProduto(@PathVariable("id") long id, ModelMap model) {
//
//		Plano plano = planoservice.findById(id);
//		
//		
//
//		model.addAttribute("plano", plano);
//		model.addAttribute("noImage", noImage);
//		model.addAttribute("categorias", categoriaService.findAll());
//		model.addAttribute("serverMessage", serverMessage);
//
//		return "plano-editar";
//	}
//	
	
	@GetMapping("/cadastro")

	public String getAll(Model model) {

		

		List<Plano> planos = planoservice.findAll();

		model.addAttribute("plano", new Plano());
		model.addAttribute("planos", planoservice.findAll());
		model.addAttribute("planos", planos);

		

		return "listaplano";
	
	
}
	
	@PostMapping("plano/editar/{id}")
	public String editar(
			@PathVariable("id") long id, 
			@ModelAttribute("plano")Plano plano) {
		
		planoservice.update(plano);
		serverMessage = "Dados alterados com sucesso!!!";
	    return "redirect:/plano/cadastro";
	}
	
	
	@GetMapping("/ver/{id}")

	public String verplano(@PathVariable("id") long id, ModelMap model) {

 

		 Plano plano = planoservice.findById(id);

 
		model.addAttribute("planos", planoservice.findAll()); 
		model.addAttribute("plano", plano);

 

		model.addAttribute("serverMessage", serverMessage);

		serverMessage = "";

		return "editar_plano";

	}
	
//	@PostMapping("/usuario/editar/{id}")
//
//	public String editar(
//
//			@PathVariable("id") long id,
//
//			@ModelAttribute("usuario") Usuario usuario) {
//
// 
//
//		usuarioService.update(usuario);
//
//		serverMessage = "Dados alterados com sucesso!!!";
//
//		return "redirect:/usuario/listacliente";
//
//	}
}
