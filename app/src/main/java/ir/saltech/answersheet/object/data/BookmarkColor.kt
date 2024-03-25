package ir.saltech.answersheet.object.data;

import android.graphics.Color;

import androidx.annotation.NonNull;

public final class BookmarkColor {
	private static final int BACKGROUND_COLOR_TRANSPARENCY = 100;
	private static final int[] BOOKMARK_COLORS = {
			Color.rgb(220, 53, 69),    //  RED
			Color.rgb(253, 126, 20),  // ORANGE
			Color.rgb(255, 193, 7),    // YELLOW
			Color.rgb(32, 201, 151),  // CYAN
			Color.rgb(13, 110, 253),   //  BLUE
			Color.rgb(214, 51, 132),  // PINK
			Color.rgb(121, 85, 72),    //  BROWN
			Color.rgb(108, 117, 125), // BLACK
			Color.rgb(224, 224, 224)   //  WHITE
	};
	private static final int[] BACKGROUND_BOOKMARK_COLORS = {
			Color.argb(BACKGROUND_COLOR_TRANSPARENCY, 220, 53, 69),    //  RED
			Color.argb(BACKGROUND_COLOR_TRANSPARENCY, 253, 126, 20),  // ORANGE
			Color.argb(BACKGROUND_COLOR_TRANSPARENCY, 255, 193, 7),    // YELLOW
			Color.argb(BACKGROUND_COLOR_TRANSPARENCY, 32, 201, 151),  // CYAN
			Color.argb(BACKGROUND_COLOR_TRANSPARENCY, 13, 110, 253),   //  BLUE
			Color.argb(BACKGROUND_COLOR_TRANSPARENCY, 214, 51, 132),  // PINK
			Color.argb(BACKGROUND_COLOR_TRANSPARENCY, 121, 85, 72),    //  BROWN
			Color.argb(BACKGROUND_COLOR_TRANSPARENCY, 108, 117, 125), // BLACK
			Color.argb(BACKGROUND_COLOR_TRANSPARENCY, 224, 224, 224)   //  WHITE
	};
	private static final String[] BOOKMARK_EMOJIS = {
			"🔴",    //  RED
			"🟠",  // ORANGE
			"🟡",    // YELLOW
			"🟢",  // CYAN
			"🔵",    //  BLUE
			"🟣",  // PINK
			"🟤",    //  BROWN
			"⚫",  // BLACK
			"⚪"     //  WHITE
	};
	private int color;
	private int backgroundColor;
	private String emoji;

	public BookmarkColor() {
	}

	public BookmarkColor(int color) {
		int colorIndex = -1;
		for (int i = 0; i < BOOKMARK_COLORS.length; i++) {
			if (color == BOOKMARK_COLORS[i]) {
				colorIndex = i;
				break;
			}
		}
		if (colorIndex >= 0) {
			this.color = BOOKMARK_COLORS[colorIndex];
			this.backgroundColor = BACKGROUND_BOOKMARK_COLORS[colorIndex];
			this.emoji = BOOKMARK_EMOJIS[colorIndex];
		}
	}

	public BookmarkColor(int colorIndex, boolean unused) {
		this.color = BOOKMARK_COLORS[colorIndex];
		this.backgroundColor = BACKGROUND_BOOKMARK_COLORS[colorIndex];
		this.emoji = BOOKMARK_EMOJIS[colorIndex];
	}

	public int getBackgroundColor() {
		return backgroundColor;
	}

	public int getColor() {
		return color;
	}

	@NonNull
	public String getEmoji() {
		return emoji;
	}

	@NonNull
	@Override
	public String toString() {
		return "BookmarkColor{" +
				"color=" + color +
				", backgroundColor=" + backgroundColor +
				", emoji='" + emoji + "'" +
				'}';
	}
}
