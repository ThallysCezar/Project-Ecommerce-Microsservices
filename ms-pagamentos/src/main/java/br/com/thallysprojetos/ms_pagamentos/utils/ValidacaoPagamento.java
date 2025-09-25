package br.com.thallysprojetos.ms_pagamentos.utils;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PagamentoValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidacaoPagamento {

    String message() default "Dados de pagamento inválidos para o tipo de pagamento selecionado.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}