package maze;

public class IncompleteMapException extends Throwable {
	public IncompleteMapException(String errorMessage) {
		super(errorMessage);
	}
}
