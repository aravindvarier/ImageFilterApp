import java.io.File;
import java.io.IOException;
import java.awt.image.*; 
import javax.imageio.*;
import java.util.Scanner;

public class greyscale{
	public static int inverter(int px){
		int alpha=(px>>24)&0xff;
		int new_pixel_val=(0xffffffff-px)|0xff000000;
		return new_pixel_val;
	}
	public static int grey_maker(int px){
		int alpha=(px>>24)&0xff;
		int r=(px>>16)&0xff;
		int g=(px>>8)&0xff;
		int b=(px)&0xff;
		int average=(r+g+b)/3;
		int new_pixel_val = alpha<<24|average<<16|average<<8|average;
		return new_pixel_val;
	}

	public static int sepia(int px){
		int alpha=(px>>24)&0xff;
		int r=(px>>16)&0xff;
		int g=(px>>8)&0xff;
		int b=(px)&0xff;
		r *= 0.43;
		g *= 0.259;
		b *= 0.078;
		int new_pixel_val = alpha<<24|r<<16|g<<8|b;
		return new_pixel_val;
	}

	public static int keepred(int px){
		int new_pixel_val=px&0xffff0000;
		return new_pixel_val;
	}

	public static int merger(int px1, int px2, float alpha)
	{
		int res_alpha=0xff;
		int r1=((px1>>16)&0xff);
		r1*=alpha;
		int g1=((px1>>8)&0xff);
		g1*=alpha;
		int b1=(px1&0xff);
		b1*=alpha;
		int r2=((px2>>16)&0xff);
		r2*=(1-alpha);
		int g2=((px2>>8)&0xff);
		g2*=(1-alpha);
		int b2=(px2&0xff);
		b2*=(1-alpha);
		int new_pixel_val=res_alpha<<24|(r1+r2)<<16|(g1+g2)<<8|(b1+b2);
		return new_pixel_val;
	}


	public static void main(String[] args) {
		while(true){
			BufferedImage img= null;
			BufferedImage img2= null;
			File f1=null;
			File f2=null;
			try{
				f1= new File("/home/aravind/javastuff/Images/lenna.png");
				f2= new File("/home/aravind/javastuff/Images/landscape.jpeg");
				img=ImageIO.read(f1);
				img2=ImageIO.read(f2);
			}
			catch(IOException e){
				System.out.println(e);
			}
			int height = img.getHeight();
			int width= img.getWidth();
			BufferedImage result_img= new BufferedImage(width,height,6);

	
			System.out.printf("Height is:%d\t Width is:%d\n",height, width);
			System.out.println("0. Exit 1. Greyscale 2. Invert 3. OnlyRed 4. Sepia 5.Blur 6.Merge");
			System.out.println("Enter your choice");
			Scanner input = new Scanner(System.in);
			int number = input.nextInt();

			String opfile="";
			switch(number){
				case 1:
					for(int y=0;y<height;y++){
						for(int x=0;x<width;x++){
							int pixel_val=img.getRGB(x,y);
							int new_pixel_val=grey_maker(pixel_val);
							img.setRGB(x,y,new_pixel_val);
						}
					}
					opfile="lennagrey";
					break;
				case 2:
					for(int y=0;y<height;y++){
						for(int x=0;x<width;x++){
							if((x+y)%2==1)
								continue;
							int pixel_val=img.getRGB(x,y);
							int new_pixel_val=inverter(pixel_val);
							img.setRGB(x,y,new_pixel_val);
						}
					}
					opfile="lennaginvert1";
					break;
				case 3:
					for(int y=0;y<height;y++){
						for(int x=0;x<width;x++){
							int pixel_val=img.getRGB(x,y);
							int new_pixel_val=keepred(pixel_val);
							img.setRGB(x,y,new_pixel_val);
						}
					}
					opfile="lennaonlyred";
					break;
				case 4:
					for(int y=0;y<height;y++){
						for(int x=0;x<width;x++){
							int pixel_val=img.getRGB(x,y);
							int new_pixel_val=sepia(pixel_val);
							img.setRGB(x,y,new_pixel_val);
						}
					}
					opfile="lennasepia";
					break;
				case 5:
					System.out.println("Enter kernel size");
					int kernel = input.nextInt();
					int offset= (kernel-1)/2;
					for(int i=0;i<height;i++){
						for(int j=0;j<width;j++){
							int average_r=0, average_g=0, average_b=0,alpha=0;
								int count =0;
								for(int m=i-offset;(m<=i+offset);m++){
									for(int n=j-offset;(n<=j+offset);n++){
										if(m < 0 || n < 0 || m >= height || n >= width)
											continue;
										
										count++;
										int pixel_val=img.getRGB(m,n);
										alpha=(pixel_val>>24)&0xff;
										int r=(pixel_val>>16)&0xff;
										int g=(pixel_val>>8)&0xff;
										int b=(pixel_val)&0xff;
										average_r+=r;
										average_g+=g;
										average_b+=b;
									}
								}
								average_r/=(count);
								average_g/=(count);
								average_b/=(count);
								int new_pixel_val=alpha<<24|average_r<<16|average_g<<8|average_b;
								result_img.setRGB(i,j,new_pixel_val);
				
						}
					}
					opfile="lennablur";
					break;
				case 6:
					System.out.println("Enter alpha between 0 - 1");
					float alpha = input.nextFloat();
					for(int y=0;y<height;y++){
						for(int x=0;x<width;x++){
							int pixel_val1=img.getRGB(x,y);
							int pixel_val2=img2.getRGB(x,y);
							int new_pixel_val=merger(pixel_val1,pixel_val2,alpha);
							result_img.setRGB(x,y,new_pixel_val);
						}
					}
					opfile="lennamerge"+Float.toString(alpha);
					break;
				case 6:
					System.out.println("Enter alpha between 0 - 1");
					float alpha = input.nextFloat();
					for(int y=0;y<height;y++){
						for(int x=0;x<width;x++){
							int pixel_val1=img.getRGB(x,y);
							int pixel_val2=img2.getRGB(x,y);
							int new_pixel_val=merger(pixel_val1,pixel_val2,alpha);
							result_img.setRGB(x,y,new_pixel_val);
						}
					}
					opfile="lennamax";
					break;
				case 0:
					System.exit(0);
				default:
					System.out.println("Invalid input");

			}

			try{
				File outputfile= new File("/home/aravind/javastuff/Images/"+opfile+".png");
				ImageIO.write(result_img,"png",outputfile);
			}
			catch(IOException e){
				System.out.println(e);
			}
		}
	}
}