package com.ruyicai.weixin.util;

public class DoubleBall {

	private static final int RED_BALL_LENGTH = 33;
	private static final int BLUE_BALL_LENGTH = 16;
	private static final int RESULT_LENGTH = 7;

	public static void main(String[] args) {

		int result[] = getDoubleBallNums();

		String split;
		for (int i = 0; i < RESULT_LENGTH; ++i) {
			split = i == RESULT_LENGTH - 2 ? " - " : " ";
			System.out.print(result[i] + split);
		}
	}

	public static int[] getDoubleBallNums() {
		int reds[] = new int[RED_BALL_LENGTH];
		int blues[] = new int[BLUE_BALL_LENGTH];
		int result[] = new int[RESULT_LENGTH];

		for (int i = 0; i < RED_BALL_LENGTH; ++i) {
			reds[i] = i + 1;
		}
		for (int i = 0; i < BLUE_BALL_LENGTH; ++i) {
			blues[i] = i + 1;
		}

		// red ball
		int index = 0;
		for (int i = 0; i < RESULT_LENGTH - 1; ++i) {
			index = (int) (Math.random() * RED_BALL_LENGTH);
			// Avoid duplicate
			if (reds[index] == 0) {
				// Reselect if duplicate
				i--;
				continue;
			} else {
				// set first value and the larger value
				if (i == 0 || reds[index] > result[i - 1]) {
					result[i] = reds[index];
				} else {
					// sort
					for (int k = i - 1; k >= 0; --k) {
						if (reds[index] < result[k]) {
							result[k + 1] = result[k];
							result[k] = reds[index];
						}
					}
				}
				// Mark 0 for selected number
				reds[index] = 0;
			}
		}

		// blue ball
		result[RESULT_LENGTH - 1] = blues[(int) (Math.random() * BLUE_BALL_LENGTH)];

		return result;
	}

	public static String getDoubleBallNumsByString() {
		int[] ballNums = getDoubleBallNums();
		String result = "";
		for (int i = 0; i < RESULT_LENGTH; ++i) {
			if(i == RESULT_LENGTH - 1)
				result += "~";
				
			if (ballNums[i] < 10)
				result += "0" + String.valueOf(ballNums[i]);
			else
				result += String.valueOf(ballNums[i]);
		}

		return result;
	}

	public static String[] getDoubleBallsByString(int betsNum) {

		String results[] = new String[betsNum];

		for (int i = 0; i < betsNum; ++i) {
			results[i] = getDoubleBallNumsByString();
		}

		return results;
	}
}