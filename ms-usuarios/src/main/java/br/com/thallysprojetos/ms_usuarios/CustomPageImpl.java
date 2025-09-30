package br.com.thallysprojetos.ms_usuarios;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class CustomPageImpl<T> extends PageImpl<T> {

    // Construtor obrigatório para a desserialização do Jackson
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public CustomPageImpl(@JsonProperty("content") List<T> content,
                          @JsonProperty("number") int number,
                          @JsonProperty("size") int size,
                          @JsonProperty("totalElements") long totalElements,
                          @JsonProperty("pageable") Pageable pageable) {
        super(content, pageable, totalElements);
    }

}
