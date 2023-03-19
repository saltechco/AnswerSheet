package ir.saltech.answersheet.object.data;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Bookmarks extends Things {
	private List<Bookmark> bookmarks;

	public Bookmarks() {
		bookmarks = new ArrayList<>();
	}

	public List<Thing> getThings() {
		return super.convertToThings(bookmarks);
	}

	public Bookmarks(@NonNull List<Bookmark> bookmarks) {
		this.bookmarks = bookmarks;
	}

	public List<Bookmark> getBookmarks() {
		return bookmarks;
	}

	public void setBookmarks(List<Bookmark> bookmarks) {
		this.bookmarks = bookmarks;
	}

	public void addBookmark(Bookmark c, int position) {
		bookmarks.add(position, c);
	}

	public void addBookmark(Bookmark c) {
		bookmarks.add(c);
	}

	public void removeBookmark(int index) {
		bookmarks.remove(index);
	}

	public boolean isBookmarkAvailable(Bookmark c) {
		boolean isExists = false;
		for (Bookmark cse : bookmarks) {
			Log.v("TAG", "com.saltechgroup. eee" + cse);
			if (cse.getName().equals(c.getName())) {
				isExists = true;
				break;
			}
		}
		return !isExists;
	}

	@NonNull
	@Override
	public String toString() {
		return "Bookmarks{" +
				"bookmarks=" + bookmarks +
				'}';
	}
}
