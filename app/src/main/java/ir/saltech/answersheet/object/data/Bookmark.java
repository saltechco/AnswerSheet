package ir.saltech.answersheet.object.data;

import androidx.annotation.NonNull;

public class Bookmark extends Thing {
    public static final String NONE = "none";
    private String name = NONE;
    private BookmarkColor pinColor = new BookmarkColor();
    private boolean bookmarkedQuestionsWanted;

    public Bookmark(@NonNull String name) {
        this.name = name;
    }

    public Bookmark() {
    }

    public boolean isBookmarkedQuestionsWanted() {
        return bookmarkedQuestionsWanted;
    }

    public void setBookmarkedQuestionsWanted(boolean bookmarkedQuestionsWanted) {
        this.bookmarkedQuestionsWanted = bookmarkedQuestionsWanted;
    }

    public BookmarkColor getPinColor() {
        return pinColor;
    }

    public void setPinColor(BookmarkColor pinColor) {
        this.pinColor = pinColor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NonNull
    @Override
    public String toString() {
        return "Bookmark{" +
                "name='" + name + '\'' +
                ", pinColor=" + pinColor +
                ", bookmarkedQuestionsWanted=" + bookmarkedQuestionsWanted +
                '}';
    }
}
