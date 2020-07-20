import java.util.Scanner;
import java.lang.reflect.Field;
import java.util.Arrays;

public class CoffeeMachine
{
	public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException 
	{
		Scanner scan = new Scanner(System.in);

		String input;
		Machine mac1 = new Machine();

		do 
		{
			mac1.state = State.WAITING;

			System.out.println(	"Write action:\n" +
								"- buy\n" +
								"- fill\n" +
								"- take\n" +
								"- remaining\n" +
								"- exit\n");

			input = scan.next();
			mac1.waiting(input);


		} while (mac1.state != State.EXIT);

		scan.close();
	}

}

class Machine
{
	Scanner scan = new Scanner(System.in);

	int[] supply = new int[]
	//water		milk	beans	cups	money
	{	400,	540,	120,	9,		550	};

	final int sMoney = supply.length-1;

	Drink[] drinks =
	{
		new Drink("espresso",	250,	0,		16,		4),
		new Drink("latte",		350,	75,		20,		7),
		new Drink("cappuccino",	200,	100,	12,		6)
	};

	State state;

	/*public boolean checkSupply(int[] s, int[][] m)
	{
		int check=0;
		for (int i = 0; i < s.length; i++, check=0)
		{
			for (int j = 0; j < m.length; j++)
			{
				if (s[j] - m[i][j] > 0)
				{
					check++;
				}
				
			}
			if (check == s.length)
			{
				return true;
			}
			
		}

		return false;
	}*/

	public int arrayMin(int[] a)
	{
		int min = a[0];
		for (int i=1; i < a.length; i++)
		{
			if (a[i] < min)
			{
				min = a[i];
			}
		}

		return min;
	}

	public void waiting(String userAction) throws IllegalArgumentException, IllegalAccessException
	{
		/*System.out.println(	"Write action:\n");

		for (int i=0; i < ; i++)
		{
			System.out.println("");
		}
		System.out.println(	"Write action:\n" +
							"- buy\n" +
							"- fill\n" +
							"- take\n" +
							"- remaining\n" +
							"- exit\n");*/

		this.state = state.setState(userAction);

		action();
	}

	public void buy() throws IllegalArgumentException, IllegalAccessException 
	{

		int[] used = new int[]
		//water		milk	beans	cups	money
		{	0,		0,		0,		0,		0	};

		int i, j, k;

		int maxCups = 0;
		String selection;
		int drink = 0;
		boolean back = false;


		System.out.println("What do you want to buy:");

		for (i=0; i < drinks.length; i++)
		{
			System.out.println(i+1 + ". " + drinks[i].name);
		}

		System.out.println(	"\t╔═══════════════╗\n" +
							"\t║     BACK      ║\n" +
							"\t╚═══════════════╝");

		selection = scan.next();
		if (selection.equals("back"))
		{
			back = true;
		}
		else
		{
			drink = Integer.parseInt(selection);
			while (drink < 1 || drink > drinks.length)
			{
				System.out.print("You can choose only between 1 and " + i + " or back: ");
				selection = scan.next();
				if (selection.equals("back"))
				{
					back = true;
					break;
				}
				else
				{
					drink = Integer.parseInt(selection);
				}
			}
			drink--;
		}

		if (back == true)
		{
			return;
		}

		int totItems=1;  // 1 because there will always be at least the cup item

		Field[] fields = drinks[drink].getClass().getDeclaredFields();
		for (Field f : fields) 
		{
			Object v = f.get(drinks[drink]);
			if (v instanceof Integer) 
			{
				if (!(f.getName()).equals("cost")) 
				{
					totItems++;
				}
			}
		}

		int uCups = used.length-2, uMoney = used.length-1;
		used[uCups] = 1;

		int[] quantities = new int[uMoney];

		String[] items = new String[totItems];
		String[] missingItems = new String[items.length];
		
		int ii, jj;
		for (i=0, j=0, k=0, ii=0, jj=0; i < fields.length; i++)
		{
			Field f = fields[i];
			Object v = f.get(drinks[drink]);
            if (v instanceof Integer)
            {

				if (j == uCups) 
				{
					used[j+1] = (int) v * used[uCups];
				}
				else
				{
					used[j] = (int) v * used[uCups];
				}

				if (k < uMoney) 
				{
					
					if (k == uCups)
					{
						items[k] = "cups";
						quantities[k] = supply[k];
					}
					else
					{
						items[k] = f.getName();
					}
					
					if ((int) v != 0)
					{
						quantities[ii] = supply[j]/(int) v;

						if (quantities[ii] == 0)
						{
							missingItems[jj] = items[k];
							jj++;
						}
						ii++;
						
					}

					k++;
				}
				j++;
			}

		}

		maxCups = arrayMin(Arrays.copyOfRange(quantities, 0, ii));

		if (maxCups >= used[uCups])
		{
			System.out.println(	"I have enough resources, making you a coffee");
			for (i=0; i < used.length; i++)
			{
				if (i == used.length-1) 
				{
					supply[i] += used[i];
				} else 
				{
					supply[i] -= used[i];
				}
			}
		}
		else
		{
			System.out.print("Sorry not enough:\n");

			for (i = 0; i < jj; i++)
			{
				System.out.println("- " + missingItems[i]);
			}
		}
	}

	public void take() 
	{
		System.out.println("I gave you $" + supply[sMoney]);
		supply[sMoney] = 0;
	}

	public void fill() 
	{
		Field[] fields = drinks[0].getClass().getDeclaredFields();

		for (int i = 0; i < fields.length; i++) {
			
		}
		System.out.println("Write how many ml of water you want to add: ");
		supply[0] += scan.nextInt();

		System.out.println("Write how many ml of milk you want to add: ");
		supply[1] += scan.nextInt();

		System.out.println("Write how many g of beans you want to add: ");
		supply[2] += scan.nextInt();

		System.out.println("Write how many cups you want to add: ");
		supply[3] += scan.nextInt();
	}

	public void remaining() throws IllegalArgumentException, IllegalAccessException 
	{
		Field[] fields = drinks[0].getClass().getDeclaredFields();

		System.out.println("The coffee machine has:\n");
		for (int i = 0, j=0; j < supply.length; i++)
		{
			if (i < fields.length)
			{
				Field f = fields[i];
				Object v = f.get(drinks[0]);
				if (v instanceof Integer && j < supply.length-2) 
				{
					{
						System.out.println(supply[j] + " " + f.getName());
						j++;
					}
				}
			}

			if (j == supply.length-2) 
			{
				System.out.println(supply[j] + " of disposable cups");
				j++;
			}
			else if (j == supply.length-1)
			{
				System.out.println(supply[j] + " of money\n");
				j++;
			}

		}
		
	}

	public void action() throws IllegalArgumentException, IllegalAccessException
	{

		switch (this.state)
		{
			/*case WAITING:
				waiting(userAction);
				break;*/

			case BUY:

				buy();
				break;

			case FILL:

				fill();
				break;

			case TAKE:
				
				take();
				break;

			case REMAIN:
				
				remaining();
				break;

			case EXIT:
				//System.out.println("See you soon!");
				break;

			default:
				System.out.println("Unknown action");

				break;
		}	

	}
}

enum State
{
	WAITING,
	BUY,
	FILL,
	TAKE,
	REMAIN,
	EXIT;

	public State setState(String action)
	{
		switch (action) 
		{
			case "buy":
				return BUY;


			case "fill":
				return FILL;

			case "take":
				return TAKE;

			case "remaining":
				return REMAIN;

			case "exit":
				return EXIT;
		
			default:
				return this;

		}

	}
}

class Drink
{
	String name;

	int water;
	int milk;
	int beans;

	int cost; // keep the cost as the last field

	Drink
	(
		String name,

		int water,
		int milk,
		int beans,
	
		int cost
	)
	{
		this.name = name;

		this.water	= water;
		this.milk	= milk;
		this.beans	= beans;
	
		this.cost	= cost;
	}

}