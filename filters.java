import java.io.File;
import java.io.IOException;
import java.awt.image.*; 
import javax.imageio.*;
import java.util.Scanner;
import java.util.*; 


public class filters{
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
				f1= new File("./Images/landscape.jpeg");
				f2= new File("./Images/landscape.jpeg");
				img=ImageIO.read(f1);
				img2=ImageIO.read(f2);
			}
			catch(IOException e){
				System.out.println(e);
			}
			int height = img.getHeight();
			int width= img.getWidth();
			BufferedImage result_img= new BufferedImage(width,height,5);

	
			System.out.printf("Height is:%d\t Width is:%d\n",height, width);
			System.out.println("0. Exit 1. Greyscale 2. Invert 3. OnlyRed 4. Sepia 5.Blur 6.Merge 7. MostCommon 8.Otsu Thresholding");
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
					float alphaFloat = input.nextFloat();
					for(int y=0;y<height;y++){
						for(int x=0;x<width;x++){
							int pixel_val1=img.getRGB(x,y);
							int pixel_val2=img2.getRGB(x,y);
							int new_pixel_val=merger(pixel_val1,pixel_val2,alphaFloat);
							result_img.setRGB(x,y,new_pixel_val);
						}
					}
					opfile="lennamerge"+Float.toString(alphaFloat);
          			break;
          
				case 7:
					Map< String,int[]> hm = new HashMap< String,int[]>();
					hm.put("black", new int[]{0x00,0x00,0x00,0}); 
					hm.put("red", new int[]{0xff,0x00,0x00,0}); 
					hm.put("green", new int[]{0x00,0xff,0x00,0}); 
					hm.put("blue", new int[]{0x00,0x00,0xff,0});
					hm.put("yellow", new int[]{0xff,0xff,0x00,0}); 
					hm.put("cyan", new int[]{0x00,0xff,0xff,0}); 
					hm.put("magenta", new int[]{0xff,0x00,0xff,0});
					hm.put("white", new int[]{0xff,0xff,0xff,0});
					
					
					System.out.println("Enter kernel size");
					kernel = input.nextInt();
					offset= (kernel-1)/2;
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
					
									double min = 9999;
									String cur = "";
									Set< Map.Entry< String,int[]> > st = hm.entrySet();    

									for (Map.Entry< String,int[]> me:st){ 
										int r1 =me.getValue()[0];
										int g1 =me.getValue()[1];
										int b1 =me.getValue()[2];
										double sum = (r1-r)*(r1-r) + (g1-g)*(g1-g) + (b1-b)*(b1-b);
										// System.out.println(sum+"-"+Math.sqrt(sum));
										if(min > Math.sqrt(sum)){
											cur = me.getKey();
											min = Math.sqrt(sum);
										}
									} 
									// System.out.println("color"+cur);
									int temp[] = hm.get(cur);
									temp[3]++;
									hm.put(cur,temp);
									cur="";
								}
							}
							// Set< Map.Entry< String,int[]> > st = hm.entrySet();
							//   for (Map.Entry< String,int[]> me:st){ 
							//     System.out.println(me.getKey()+me.getValue()[3]);
							// }
							int max = -1;
							String cur = "";
							Set< Map.Entry< String,int[]> > st = hm.entrySet();    
							for (Map.Entry< String,int[]> me:st){ 
								if(max < me.getValue()[3]){
									cur = me.getKey();
									max = me.getValue()[3];
								}
							}
							int new_pixel_val=0xff<<24|hm.get(cur)[0]<<16|hm.get(cur)[1]<<8|hm.get(cur)[2];
							result_img.setRGB(i,j,new_pixel_val);
							hm.put("black", new int[]{0x00,0x00,0x00,0}); 
							hm.put("red", new int[]{0xff,0x00,0x00,0}); 
							hm.put("green", new int[]{0x00,0xff,0x00,0}); 
							hm.put("blue", new int[]{0x00,0x00,0xff,0});
							hm.put("yellow", new int[]{0xff,0xff,0x00,0}); 
							hm.put("cyan", new int[]{0x00,0xff,0xff,0}); 
							hm.put("magenta", new int[]{0xff,0x00,0xff,0});
							hm.put("white", new int[]{0xff,0xff,0xff,0});                      
						}
					}
					opfile="lennamax1";
					break;
				case 8:	
					int t;
					BufferedImage greyscale= new BufferedImage(width,height,5);
					BufferedImage bwimage= new BufferedImage(width,height,5);
					for(int y=0;y<height;y++){
						for(int x=0;x<width;x++){
							int pixel_val=img.getRGB(x,y);
							int new_pixel_val=grey_maker(pixel_val);
							greyscale.setRGB(x,y,new_pixel_val);
						}
					}
					int hist[]=new int[256];
					float bcv[]=new float[256];
					for(int i=0;i<256;i++)
						bcv[i]=0;
					int max_index=0;
					for(int y=0;y<height;y++){
						for(int x=0;x<width;x++){
							int pixel_val=greyscale.getRGB(x,y);
							int r=(pixel_val>>16)&0xff;
							hist[r]++;
						}
					}
					
					for(t=0;t<256;t++){
						float wb=0,wf=0,meanb=0,meanf=0;
						for(int i=0;i<t;i++){
							wb+=hist[i];
							meanb+=(i*hist[i]);
						}
						if(wb!=0)
							meanb/=wb;
						else
							meanb=0;
						wb/=(width*height);

						for(int i=t;i<256;i++){
							wf+=hist[i];
							meanf+=(i*hist[i]);
						}
						if(wf!=0)
							meanf/=wf;
						else
							meanf=0;
						wf/=(width*height);
						bcv[t]=(wb*wf)*(meanb-meanf)*(meanb-meanf);
						if(bcv[t]>=bcv[max_index])
							max_index=t;
					}
					System.out.println(max_index);
					for(int y=0;y<height;y++){
						for(int x=0;x<width;x++){
							int pixel_val=greyscale.getRGB(x,y);
							int r=(pixel_val>>16)&0xff;
							int new_pixel_val;
							if(r>max_index)
								new_pixel_val=0xffffffff;
							else
								new_pixel_val=0x00000000;
							bwimage.setRGB(x,y,new_pixel_val);
						}
					}
					result_img=bwimage;
					opfile="otsu";
					break;
				case 0:
					System.exit(0);
				default:
					System.out.println("Invalid input");

			}

			try{
				File outputfile= new File("./Images/"+opfile+".png");
				ImageIO.write(result_img,"png",outputfile);
			}
			catch(IOException e){
				System.out.println(e);
			}
		}
	}
}