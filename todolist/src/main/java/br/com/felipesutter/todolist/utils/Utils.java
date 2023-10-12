package br.com.felipesutter.todolist.utils;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

public class Utils {

    // aqui ele vai copiar o objeto e separar os valores nulos dos não nulos
    // source - de onde vem, target - pra onde vai
    public static void copyNonNullProperties(Object source, Object target) {
        BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
    }

    // aqui ele vai pegar todos os valores nulos
    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);

        // gera um array com todas as propriedades do objeto
        PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();

        // vai pegar os valores do objeto, se o valor for nulo, coloca dentro do set de
        // valores vazios
        for (PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                emptyNames.add(pd.getName());
            }
        }
        // armazena esses valores no array e dps converte eles pra um array de string
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

}
