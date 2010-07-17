/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package brix.plugin.file.util;

import java.awt.Rectangle;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;
import org.im4java.process.Pipe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wickeria at gmail.com
 */
public class ImageMagicProcessor {

	private static final Logger logger = LoggerFactory.getLogger(ImageMagicProcessor.class);

	// scale% Height and width both scaled by specified percentage.
	// scale-x%xscale-y% Height and width individually scaled by specified
	// percentages. (Only one % symbol needed.)
	// width Width given, height automagically selected to preserve aspect
	// ratio.
	// xheight Height given, width automagically selected to preserve aspect
	// ratio.
	// widthxheight Maximum values of height and width given, aspect ratio
	// preserved.
	// widthxheight^ Minimum values of width and height given, aspect ratio
	// preserved.
	// widthxheight! Width and height emphatically given, original aspect ratio
	// ignored.
	// widthxheight> Change as per widthxheight but only if an image dimension
	// exceeds a specified dimension.
	// widthxheight< Change dimensions only if both image dimensions exceed
	// specified dimensions.
	// area@ Resize image to have specified area in pixels. Aspect ratio is
	// preserved.

	public static InputStream createThumbnail(InputStream in, int width, int height, boolean centerCrop)
			throws Exception {

		try {
			IMOperation op = new IMOperation();
			op.addImage("-");
			if (centerCrop) {
				op.thumbnail(width, height, '^');
				op.gravity("center");
				op.crop(width, height, 0, 0);
			} else {
				op.thumbnail(width, height, '>');
			}
			op.addImage("-");
			Pipe pipeIn = new Pipe(in, null);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			Pipe pipeOut = new Pipe(null, out);

			// set up command
			ConvertCmd convert = new ConvertCmd();
			// convert.setAsyncMode(true);
			convert.setInputProvider(pipeIn);
			convert.setOutputConsumer(pipeOut);

			// convert.createScript("/home/dan/tmp/log.txt", op);
			convert.run(op);
			logger.info("createThumbnail() :{}", convert.toString());

			return new ByteArrayInputStream(out.toByteArray());
		} finally {
			in.close();
		}
	}

	public static InputStream createCrop(InputStream in, Rectangle rectangle) {

		IMOperation op = new IMOperation();
		op.addImage("-");

		op.crop(rectangle.height, rectangle.width, rectangle.x, rectangle.y);

		op.addImage("-");
		Pipe pipeIn = new Pipe(in, null);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Pipe pipeOut = new Pipe(null, out);

		// set up command
		ConvertCmd convert = new ConvertCmd();
		convert.setInputProvider(pipeIn);
		convert.setOutputConsumer(pipeOut);
		try {
			// convert.createScript("/home/dan/tmp/log.txt", op);
			convert.run(op);
			logger.info("createCrop() :{}", convert.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ByteArrayInputStream(out.toByteArray());

	}

}
