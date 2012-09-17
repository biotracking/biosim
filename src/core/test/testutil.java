package core.test;

import java.awt.Frame;

import core.util.CircularBufferEnumeration;
import core.util.CircularBufferException;
import core.util.DialogMessage;
import core.util.DialogMessageJoke;
import core.util.FilenameFilterByEnding;
import core.util.TBVersion;
import core.util.Vec2;

public class testutil {
	public static void main(String[] args) {
		Frame frame = new Frame();

		CircularBuffer circularBuffer = new CircularBuffer();
		CircularBufferEnumeration circularBufferEnumeration = new CircularBufferEnumeration(
				circularBuffer);
		CircularBufferException circularBufferException = new CircularBufferException();
		DialogMessage dialogMessage = new DialogMessage(frame, "", "");
		DialogMessageJoke dialogMessageJoke = new DialogMessageJoke(frame, "",
				"");
		FilenameFilterByEnding filenameFilterByEnding = new FilenameFilterByEnding(
				"");
		TBVersion tbVersion = new TBVersion();
		BioSimVersion biosimVersion = new BioSimVersion();
		Units units = new Units();
		Vec2 vec2 = new Vec2();

		if (circularBuffer.test() == 0 && circularBufferEnumeration.test() == 0
				&& circularBufferException.test() == 0
				&& dialogMessage.test() == 0 && dialogMessageJoke.test() == 0
				&& filenameFilterByEnding.test() == 0 && tbVersion.test() == 0
				&& biosimVersion.test() == 0 && units.test() == 0
				&& vec2.test() == 0) {
			System.exit(0);
		} else {
			System.exit(1);
		}
	}
}
