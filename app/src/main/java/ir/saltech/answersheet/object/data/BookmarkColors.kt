package ir.saltech.answersheet.object.data;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class BookmarkColors {
	private List<BookmarkColor> bookmarkColors;

	public BookmarkColors() {
		bookmarkColors = new ArrayList<>();
	}

	public List<BookmarkColor> getBookmarkColors() {
		return bookmarkColors;
	}

	public void setBookmarkColors(List<BookmarkColor> bookmarkColors) {
		this.bookmarkColors = bookmarkColors;
	}

	public void addBookmarkColor(BookmarkColor q) {
		bookmarkColors.add(q);
	}

	@NonNull
	@Override
	public String toString() {
		return "BookmarkColors{" +
				"bookmarkColors=" + bookmarkColors +
				'}';
	}
}
