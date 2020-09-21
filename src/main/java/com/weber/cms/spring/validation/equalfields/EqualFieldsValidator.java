package com.weber.cms.spring.validation.equalfields;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EqualFieldsValidator implements ConstraintValidator<EqualFields, Object> {

    List<String> fields = new ArrayList<>();

    @Override
    public void initialize(EqualFields constraintAnnotation) {
        fields = Arrays.asList(constraintAnnotation.fields());
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        boolean isValid = true;
        if (fields != null && fields.size() > 0) {
            try {
                Object matchingValue = getFieldValue(value, fields.get(0));
                for(int i = 1; i < fields.size(); i++) {
                    Object currentValue = getFieldValue(value, fields.get(i));
                    if(matchingValue == null) {
                        if (currentValue == null) {
                            continue;
                        } else {
                            isValid = false;
                        }
                    } else {
                        isValid = matchingValue.equals(currentValue);
                    }
                    if (!isValid) {
                        break;
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
                isValid = false;
            }
        }
        return isValid;
    }

    private Object getFieldValue(Object object, String fieldName) throws Exception {
        Class<?> clazz = object.getClass();
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(object);
    }
}
