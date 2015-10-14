package biz.neustar.clouds.chat.util;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

public class HexUtil {

	public static byte[] decodeHex(String string) {

		try {

			return Hex.decodeHex(string.toCharArray());
		} catch (DecoderException ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}
	}
}
