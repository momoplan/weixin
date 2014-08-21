package com.ruyicai.weixin.util;

import java.util.Random;

public class RandomPacketUtil {

	/**
	 * 随机生成注数
	 * 
	 * @param betNum 注数
	 * @param parts 份数
	 * @return
	 */
	public static int[] getRandomPunt(int betNum, int parts)
	{
		Random rd = new Random();
		int[] p = new int[parts];
		if(parts >= betNum)
		{
			for(int i = 0; i < betNum; i ++)
			{
				p[i] = 1;
			}
		} else
		{
			int init = 1;
			int sum = 0;
			int remain = betNum - parts;
			int benchmark = 2;
			if(benchmark < remain)
				benchmark += benchmark * (betNum / parts);

			for(int i = 0; i < p.length; ++i)
			{
				int next = 0;
				if (rd.nextInt(2) == 1)
					next = rd.nextInt(benchmark);
				
				if ((sum + next) > remain)
				{
					p[i] = init;
				} else
				{
					p[i] = init + next;
					sum += next;
				}

				if (i == (p.length - 1))
				{
					if(sum < remain)
					{
						p[i] += remain - sum;
					}
				}
			}
		}

		return p;
	}
	
}
