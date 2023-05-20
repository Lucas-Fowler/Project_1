package maze;

public class IllegalMapCharacterException extends Throwable {
	public IllegalMapCharacterException(String errorMessage) {
		super(errorMessage);
	}
}
