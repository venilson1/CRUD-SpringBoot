package com.devSuperior.dsCatalog.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

import java.util.List;
import java.util.Optional;

import com.devSuperior.dsCatalog.dto.ProductDTO;
import com.devSuperior.dsCatalog.entities.Category;
import com.devSuperior.dsCatalog.entities.Product;
import com.devSuperior.dsCatalog.repositories.CategoryRepository;
import com.devSuperior.dsCatalog.repositories.ProductRepository;
import com.devSuperior.dsCatalog.services.exceptions.DatabaseException;
import com.devSuperior.dsCatalog.services.exceptions.ResourceNotFoundException;
import com.devSuperior.dsCatalog.tests.Factory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

// teste de unidade
@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

  // referncia do componente que precisa ser testado | @autowid iria injetar
  @InjectMocks
  private ProductService service;

  @Mock // simular as dependencias | test unidade que não carrega o contexto
  private ProductRepository repository;

  @Mock
  private CategoryRepository categoryRepository;

  private long existsId;
  private long notExistsId;
  private long dependentId;
  private PageImpl<Product> page;
  private Product product;
  private ProductDTO productDTO;
  private Category category;

  @BeforeEach
  void setUp() throws Exception {
    existsId = 1L;
    notExistsId = 1000L;
    dependentId = 4L;
    product = Factory.createProduct();
    productDTO = Factory.createProductDTO();
    page = new PageImpl<>(List.of(product));
    category = Factory.createCategory();

    /*
     * configurar comportamento simulado do metodo deleteById
     * when -> qual metodo deveria retornar do objeto repository o que espera desse
     * metodo
     */

    // quando passar um id que existe para o metodo do repository não retornar nada
    Mockito.doNothing().when(repository).deleteById(existsId);

    // id que não existe para o metodo do repository deve lançar uma exceção
    Mockito.doThrow(EmptyResultDataAccessException.class)
        .when(repository).deleteById(notExistsId);

    Mockito.doThrow(DataIntegrityViolationException.class)
        .when(repository).deleteById(dependentId);

    Mockito.when(repository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);

    Mockito.when(repository.save(any())).thenReturn(product);

    Mockito.when(repository.findById(existsId)).thenReturn(Optional.of(product));
    
    Mockito.when(repository.find(any(), any(), any())).thenReturn(page);

    Mockito.when(repository.findById(notExistsId)).thenReturn(Optional.empty());

    Mockito.when(repository.getOne(existsId)).thenReturn(product);

    Mockito.when(categoryRepository.getOne(existsId)).thenReturn(category);

    Mockito.when(categoryRepository.getOne(notExistsId)).thenReturn(category);

    Mockito.doThrow(ResourceNotFoundException.class)
        .when(repository).getOne(notExistsId);
  }

  @Test
  public void deleteShouldDoNothingWhenIdExists() {

    assertDoesNotThrow(() -> {
      service.delete(existsId);
    });

    // assertion do mockito para verficar se o metodo foi chamado | se foi chamado
    // apenas uma vez
    Mockito.verify(repository, Mockito.times(1)).deleteById(existsId);

  }

  @Test
  public void deleteShouldReturnResourceNotFoundExceptionWhenIdDoesNotExists() {

    assertThrows(ResourceNotFoundException.class, () -> {
      service.delete(notExistsId);
    });

    // assertion do mockito para verficar se o metodo foi chamado com id não exi
    // qtd de vezes que foi chamado -> times(1)
    Mockito.verify(repository, Mockito.times(1)).deleteById(notExistsId);
  }

  @Test
  public void deleteShouldReturnDatabaseExceptionWhenDependentId() {

    assertThrows(DatabaseException.class, () -> {
      service.delete(dependentId);
    });

    // assertion do mockito para verficar se o metodo foi chamado com id não exi
    // qtd de vezes que foi chamado -> times(1)
    Mockito.verify(repository, Mockito.times(1)).deleteById(dependentId);
  }

  @Test
  public void findAllPagedShouldReturnPage() {

    Pageable pageable = PageRequest.of(0, 10);

    Page<ProductDTO> result = service.findAllPaged(0L, "", pageable);

    assertNotNull(result);

    // assertion do mockito para verficar se o metodo foi chamado com id não exi
    //Mockito.verify(repository).findAll(pageable);

  }

  @Test
  public void findByIdShouldReturnProductDTOWhenIdExist() {

    ProductDTO product = service.findById(existsId);

    assertNotNull(product);

    Mockito.verify(repository, Mockito.times(1)).findById(existsId);
  }

  @Test
  public void findByIdShouldReturnResourceNotFoundExceptionWhenIdDoesNotExists() {

    assertThrows(ResourceNotFoundException.class, () -> {
      service.findById(notExistsId);
    });

    Mockito.verify(repository, Mockito.times(1)).findById(notExistsId);
  }

  @Test
  public void updateIdShouldReturnProductDTOWhenIdExist() {

    service.update(existsId, productDTO);
    Mockito.verify(repository, Mockito.times(1)).getOne(existsId);
  }

  @Test
  public void updateIdShouldReturnResourceNotFoundExceptionWhenIdDoesNotExists() {

    assertThrows(ResourceNotFoundException.class, () -> {
      service.update(notExistsId, productDTO);
    });

    Mockito.verify(repository, Mockito.times(1)).getOne(notExistsId);
  }

}
