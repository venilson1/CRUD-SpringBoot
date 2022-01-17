package com.devSuperior.dsCatalog.entities;

import com.devSuperior.dsCatalog.dto.UserDTO;

public class UserInsertDTO extends UserDTO {

  private String password;

  public UserInsertDTO() {
    super();
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

}
