package com.framework.loippi.utils.validator;

import com.framework.loippi.consts.Constants;
import com.framework.loippi.enus.ActivityTypeEnus;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Set;
//import org.junit.Test;

/**
 * 敏感词过滤
 *
 * @author kviuff
 */
public class WordsValidators implements ConstraintValidator<Words, String> {

    @Override
    public void initialize(Words wordsAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext content) {
        SensitivewordFilter filter = new SensitivewordFilter(ActivityTypeEnus.FILE_BASEPATH + Constants.SENSITIVE_UPLOAD_URL);
        Set<String> set = filter.getSensitiveWord(value, 1);
        int size = set.size();
        return size > 0 ? false : true;
    }

//	@Test
//	public void testWords(){
//		AnnotationDescriptor<Words> desc = new AnnotationDescriptor<Words>(Words.class);
//		Words words = AnnotationFactory.create(desc);
//		WordsValidators wordsValidate = new WordsValidators();
//		wordsValidate.initialize(words);
//		//Assert.assertTrue(!wordsValidate.isValid("123", null));
//	}

}
