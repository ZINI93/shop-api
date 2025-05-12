package com.zinikai.shop.domain.member.exception;

public class MemberRoleNotMatchException extends RuntimeException {
  public MemberRoleNotMatchException(String message) {
    super(message);
  }
}
