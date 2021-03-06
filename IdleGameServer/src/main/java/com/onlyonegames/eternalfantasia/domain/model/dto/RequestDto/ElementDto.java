package com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto;

//import java.util.List;
import lombok.Data;

@Data
public class ElementDto {
    String element;
    String value;

    public void SetValue(int element) {
        this.value = Integer.toString(element);
    }

    public void SetValue(Long element) {
        this.value = Long.toString(element);
    }

    public void SetValue(String element) {
        this.value = element;
    }

    public void SetValue(double element) {
        this.value = Double.toString(element);
    }

    public void SetElement(String element, String value) {
        this.element = element;
        this.value = value;
    }
}
