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
			int init = (int) (betNum * 0.6) / parts ; // 初始值
			if (init == 0)
				init = 1;
			
			int remain = betNum - (init * parts);
			int remainAvg = (int) (remain / parts);
			
			if (remainAvg < 3)
				remainAvg = 5;
			
			int sum = 0;
			for(int i = 0; i < p.length; ++i)
			{
				int next = 1 + rd.nextInt(remainAvg);
				
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
	
	public static void main(String[] args)
	{
		for (int i = 0; i< 50;i++)
		{
			System.out.print("测试" + i + "=====");
			int[] in = getRandomPunt(100,20);
			int sum = 0;
			for (int j = 0 ; j <in.length ; j++)
			{
				System.out.print(in[j] + ",");
				sum += in[j];
			}
			System.out.println("\r\n 总注数: " + sum + "\r\n");
		}
	}
	
}
