package blend_2bpp;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class Main {

	private static final String NAME_1 = "Pikachu";
	private static final String NAME_2 = "Geodude";

	private static final String FRONT_1 = "pic/" + NAME_1 + "/front.2bpp";
	private static final String FRONT_2 = "pic/" + NAME_2 + "/front.2bpp";
	private static final String BACK_1 = "pic/" + NAME_1 + "/back.2bpp";
	private static final String BACK_2 = "pic/" + NAME_2 + "/back.2bpp";
	private static final String FDIM_1 = "pic/" + NAME_1 + "/front.dimensions";
	private static final String FDIM_2 = "pic/" + NAME_2 + "/front.dimensions";

	private static final String OUT_FRONT = "pic_test/" + NAME_1 + "_" + NAME_2 + "front.2bpp";
	private static final String OUT_BACK = "pic_test/" + NAME_1 + "_" + NAME_2 + "back.2bpp";

	public static void main(String[] args) {

		try (
			RandomAccessFile raf_fpic1 = new RandomAccessFile(FRONT_1,  "r" );
			RandomAccessFile raf_fpic2 = new RandomAccessFile(FRONT_2,  "r" );
			RandomAccessFile raf_bpic1 = new RandomAccessFile(BACK_1,  "r" );
			RandomAccessFile raf_bpic2 = new RandomAccessFile(BACK_2,  "r" );
			RandomAccessFile raf_fdim1 = new RandomAccessFile(FDIM_1,  "r" );
			RandomAccessFile raf_fdim2 = new RandomAccessFile(FDIM_2,  "r" );
			RandomAccessFile raf_outfront = new RandomAccessFile(OUT_FRONT,  "rw");
			RandomAccessFile raf_outback = new RandomAccessFile(OUT_BACK,  "rw");
			FileChannel fc_fpic1  = raf_fpic1.getChannel();
			FileChannel fc_fpic2  = raf_fpic2.getChannel();
			FileChannel fc_bpic1  = raf_bpic1.getChannel();
			FileChannel fc_bpic2  = raf_bpic2.getChannel();
			FileChannel fc_fdim1  = raf_fdim1.getChannel();
			FileChannel fc_fdim2  = raf_fdim2.getChannel();
			FileChannel fc_outfront = raf_outfront.getChannel();
			FileChannel fc_outback = raf_outback.getChannel();
		) {

			ByteBuffer fpic1 = ByteBuffer.allocate(56 * 56 / 4);
			ByteBuffer fpic2 = ByteBuffer.allocate(56 * 56 / 4);
			ByteBuffer bpic1 = ByteBuffer.allocate(48 * 48 / 4);
			ByteBuffer bpic2 = ByteBuffer.allocate(48 * 48 / 4);
			ByteBuffer outfront = ByteBuffer.allocate(56 * 56 / 4);
			ByteBuffer outback = ByteBuffer.allocate(48 * 48 / 4);
			byte fdim1 = 0x77;
			byte fdim2 = 0x77;

			fc_fpic1.read(fpic1, 0);
			fc_fpic2.read(fpic2, 0);
			fc_bpic1.read(bpic1, 0);
			fc_bpic2.read(bpic2, 0);
			fdim1 = raf_fdim1.readByte();
			fdim2 = raf_fdim2.readByte();

			fpic1.rewind();
			fpic2.rewind();

			byte[] _temp = new byte[56 * 56 / 4];

			if (fdim1 == 0x55) {
				for (int i = 0; i < 5; i ++) {
					fpic1.get(_temp, i*7*8*2, 5*8*2);
				}
				fpic1.rewind();
				fpic1.put(_temp);
			}

			if (fdim1 == 0x66) {
				for (int i = 0; i < 6; i ++) {
					fpic1.get(_temp, i*7*8*2, 6*8*2);
				}
				fpic1.rewind();
				fpic1.put(_temp);
			}

			if (fdim2 == 0x55) {
				for (int i = 0; i < 5; i ++) {
					fpic2.get(_temp, i*7*8*2, 5*8*2);
				}
				fpic2.rewind();
				fpic2.put(_temp);
			}

			if (fdim2 == 0x66) {
				for (int i = 0; i < 6; i ++) {
					fpic2.get(_temp, i*7*8*2, 6*8*2);
				}
				fpic2.rewind();
				fpic2.put(_temp);
			}

			fpic1.rewind();
			fpic2.rewind();

			byte _s1l, _s1m, _s2l, _s2m, _out, _templ, _tempm;

			for(int n = 0; n < fpic1.capacity(); n += 2) {
				for (int b = 0; b <= 7; b ++) {
				    _s1l = (byte) ((fpic1.get(n) & (1 << b)) >> b);
				    _s1m = (byte) ((fpic1.get(n+1) & (1 << b)) >> b);
				    _s2l = (byte) ((fpic2.get(n) & (1 << b)) >> b);
				    _s2m = (byte) ((fpic2.get(n+1) & (1 << b)) >> b);
				    _out = (byte) Math.max(2 * _s1m + _s1l, 2 * _s2m + _s2l);
				    _templ = (byte) (outfront.get(n) + ((_out % 2) << b));
				    outfront.put(n, _templ);
				    _tempm = (byte) (outfront.get(n+1) + ((_out / 2) << b));
				    outfront.put(n+1, _tempm);
				}
			}

			for(int n = 0; n < bpic1.capacity(); n += 2) {
				for (int b = 0; b <= 7; b ++) {
				    _s1l = (byte) ((bpic1.get(n) & (1 << b)) >> b);
				    _s1m = (byte) ((bpic1.get(n+1) & (1 << b)) >> b);
				    _s2l = (byte) ((bpic2.get(n) & (1 << b)) >> b);
				    _s2m = (byte) ((bpic2.get(n+1) & (1 << b)) >> b);
				    _out = (byte) Math.max(2 * _s1m + _s1l, 2 * _s2m + _s2l);
				    _templ = (byte) (outback.get(n) + ((_out % 2) << b));
				    outback.put(n, _templ);
				    _tempm = (byte) (outback.get(n+1) + ((_out / 2) << b));
				    outback.put(n+1, _tempm);
				}
			}

			fc_outfront.write(outfront, 0);
			fc_outback.write(outback, 0);

		} catch (Exception e) {
			System.out.println(e.toString());
		}

	}

}
