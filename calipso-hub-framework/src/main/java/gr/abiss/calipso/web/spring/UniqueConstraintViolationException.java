package gr.abiss.calipso.web.spring;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
@ResponseStatus(value=HttpStatus.BAD_REQUEST) 
public class UniqueConstraintViolationException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;
	
	private List<String> errors;
	
	public UniqueConstraintViolationException(String message, List<String> errors){
		super(message);
		this.errors = errors;
	}

	public List<String> getErrors() {
		return errors;
	}
	
	

}
