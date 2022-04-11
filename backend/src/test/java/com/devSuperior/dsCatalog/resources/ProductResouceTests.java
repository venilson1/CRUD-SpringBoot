package com.devSuperior.dsCatalog.resources;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.devSuperior.dsCatalog.dto.ProductDTO;
import com.devSuperior.dsCatalog.services.ProductService;
import com.devSuperior.dsCatalog.services.exceptions.DatabaseException;
import com.devSuperior.dsCatalog.services.exceptions.ResourceNotFoundException;
import com.devSuperior.dsCatalog.tests.Factory;
import com.devSuperior.dsCatalog.tests.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductResouceTests {

	@Autowired
	private MockMvc mockmvc;

	@MockBean
	private ProductService service;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private TokenUtil tokenUtil;

	private Long existsId;
	private Long notExistsId;
	private Long dependentId;
	private ProductDTO productDTO;
	private PageImpl<ProductDTO> page;
	private String username;
	private String passworld;

	@BeforeEach
	void setUp() throws Exception {

		productDTO = Factory.createProductDTO();
		existsId = 1L;
		notExistsId = 1000L;
		dependentId = 3L;
		
		username = "maria@gmail.com";
		passworld = "123456";

		// PageImpl permite usar o new
		// Lis.of() -> permite instanciar uma lista com algo dentro no caso um
		// produtoDto
		page = new PageImpl<>(List.of(productDTO));

		// simulando o service

		// any() -> no caso do paged não importa o argumento
		when(service.findAllPaged(any(), any(), any())).thenReturn(page);

		when(service.findById(existsId)).thenReturn(productDTO);
		when(service.findById(notExistsId)).thenThrow(ResourceNotFoundException.class);

		when(service.update(eq(existsId), any())).thenReturn(productDTO);
		when(service.update(eq(notExistsId), any())).thenThrow(ResourceNotFoundException.class);

		when(service.insert(any())).thenReturn(productDTO);

		doNothing().when(service).delete(existsId);
		doThrow(ResourceNotFoundException.class).when(service).delete(notExistsId);
		doThrow(DatabaseException.class).when(service).delete(dependentId);
	}

	@Test
	public void findAllShouldReturnPage() throws Exception {
		ResultActions result = mockmvc.perform(get("/products").accept(MediaType.APPLICATION_JSON));

		// mediaType -> aceita como esposta o tipo JSON

		result.andExpect(status().isOk());
	}

	@Test
	public void findByIdShouldReturnProductWhenId() throws Exception {

		ResultActions result = mockmvc.perform(get("/products/{id}", existsId).accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());

		// no corpo da resposta expera um campo existente com os nomes: id|name|desc
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());
	}

	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {

		ResultActions result = mockmvc.perform(get("/products/{id}", notExistsId).accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isNotFound());
	}

	@Test
	public void updateShouldReturnProductDTOWhenIdExists() throws Exception {

		String accesToken = tokenUtil.obtainAccessToken(mockmvc, username, passworld);
		
		// coverter obj Java para string
		String jsonBody = objectMapper.writeValueAsString(productDTO);

		ResultActions result = mockmvc.perform(put("/products/{id}", existsId).content(jsonBody)
				.header("Authorization", "Bearer " + accesToken)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());
	}

	@Test
	public void updateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {

		String accesToken = tokenUtil.obtainAccessToken(mockmvc, username, passworld);
		
		String jsonBody = objectMapper.writeValueAsString(productDTO);

		ResultActions result = mockmvc.perform(put("/products/{id}", notExistsId).content(jsonBody)
				.header("Authorization", "Bearer " + accesToken)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isNotFound());
	}

	@Test
	public void insertShouldReturnCreated() throws Exception {

		String accesToken = tokenUtil.obtainAccessToken(mockmvc, username, passworld);
		
		String jsonBody = objectMapper.writeValueAsString(productDTO);

		ResultActions result = mockmvc.perform(post("/products", productDTO)
				.content(jsonBody)
				.header("Authorization", "Bearer " + accesToken)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isCreated());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());

	}

	@Test
	public void deleteShouldNoContentWhenIdExists() throws Exception {

		String accesToken = tokenUtil.obtainAccessToken(mockmvc, username, passworld);
		
		ResultActions result = mockmvc.perform(delete("/products/{id}", existsId)
				.header("Authorization", "Bearer " + accesToken)
				.accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isNoContent());

	}

	@Test
	public void deleteShouldReturnResourceNotFoundWhenIdDoesNotExists() throws Exception {

		String accesToken = tokenUtil.obtainAccessToken(mockmvc, username, passworld);

		ResultActions result = mockmvc.perform(delete("/products/{id}", notExistsId)
				.header("Authorization", "Bearer " + accesToken).accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isNotFound());

	}
}
