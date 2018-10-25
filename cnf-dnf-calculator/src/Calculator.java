import java.util.*;

public class Calculator {
	public static void main(String[] args) {
		System.out.print("Syntax : ( , ) , & , | , ~ , -> , <->\n\n>>  ");
		String data = new Scanner(System.in).nextLine();
		ArrayList<String> Literals = new ArrayList<>();
		StringTokenizer mytoken = new StringTokenizer(data, " )(<>-~&|");
		while (mytoken.hasMoreTokens()) {
			String temp = mytoken.nextToken();
			// System.out.println(temp);
			if (!Literals.contains(temp)) {
				Literals.add(temp);
			}
		}
		int[][] literalsTF = new int[(int) Math.pow(2, Literals.size())][Literals.size()];
		for (int i = 0; i < (int) Math.pow(2, Literals.size()); i++) {
			String temp = Integer.toBinaryString(i);
			while (temp.length() < Literals.size())
				temp = "0" + temp;
			for (int j = 0; j < Literals.size(); j++) {
				literalsTF[i][j] = temp.toCharArray()[j] - 48;
			}
		}
		// for (int i = 0; i < (int) Math.pow(2, Literals.size()); i++) {
		//
		// for (int j = 0; j < Literals.size(); j++) {
		// System.out.print(literalsTF[i][j] + " ");
		// }
		// System.out.println();
		// }
		ArrayList<Literal> Ldata = new ArrayList<>();
		for (int i = 0; i < Literals.size(); i++) {
			int[] tempTF = new int[(int) Math.pow(2, Literals.size())];
			for (int j = 0; j < (int) Math.pow(2, Literals.size()); j++) {
				tempTF[j] = literalsTF[j][i];
			}
			Ldata.add(new Literal(Literals.get(i), tempTF));
		}
		for (Literal literal : Ldata) {
			literal.printMe();
		}
		Stack<Literal> stack = new Stack<>();
		char[] Cdata = data.toCharArray();
		// data = "(" + data + ")";
		data = data.replace("(", " ( ");
		data = data.replace(")", " )");
		data = data.replace("~", " ~ ");
		data = data.replace("&", " & ");
		data = data.replace("|", " | ");
		data = data.replace("<->", " <-> ");
		data = data.replace("->", " -> ");
		mytoken = new StringTokenizer(data, " ");
		while (mytoken.hasMoreTokens()) {
			String temp = mytoken.nextToken();
			if (temp.equals("&")) {
				stack.push(new Literal("&", null));
			} else if (temp.equals("|")) {
				stack.push(new Literal("|", null));
			} else if (temp.equals("->")) {
				stack.push(new Literal("->", null));
			} else if (temp.equals("<->")) {
				stack.push(new Literal("<->", null));
			} else if (temp.equals("~")) {
				stack.push(new Literal("~", null));

			} else if (temp.equals(")")) {
				Literal b = stack.pop();
				Literal func = stack.pop();
				if (func.name.equals("~")) {
					Literal ans = Literal.opHandler(b, null, func);
					ans.printMe();
					stack.push(ans);
				} else {
					Literal a = stack.pop();
					Literal ans = Literal.opHandler(a, b, func);
					ans.printMe();
					stack.push(ans);
				}
			} else {
				for (Literal literal : Ldata) {
					if (temp.equals(literal.name)) {
						stack.push(literal);
						break;
					}
				}
			}
		}
		Literal FinalAnswer = stack.pop();
		System.out.println("\n>>Truth Table Completed");
		System.out.println("\nCNF : " + CNF(Ldata, FinalAnswer) + "\n");
		System.out.println("DNF : " + DNF(Ldata, FinalAnswer));
		System.out.println("\n>>Done!");

	}

	public static String CNF(ArrayList<Literal> literalData, Literal FinalAnswer) {
		String result = " ";
		for (int i = 0; i < FinalAnswer.myTF.length; i++) {
			boolean flag = false;
			if (FinalAnswer.myTF[i] == 0) {
				String temp = "(";
				for (Literal literal : literalData) {
					if (flag) {
						temp += " |";
					} else {
						flag = true;
					}
					if (literal.myTF[i] == 1)
						temp = temp + " ~" + literal.name;
					else
						temp = temp + " " + literal.name;
				}
				temp = temp + " )";
				result = result + " & " + temp;
			}
		}
		result = result.replace("  & (", " (");
		if (result.equals(" "))
			return "( " + literalData.get(0).name + " | ~" + literalData.get(0).name + " )" + "  *all true case";
		return result;
	}

	public static String DNF(ArrayList<Literal> literalData, Literal FinalAnswer) {
		String result = " ";
		for (int i = 0; i < FinalAnswer.myTF.length; i++) {
			boolean flag = false;
			if (FinalAnswer.myTF[i] == 1) {
				String temp = "(";
				for (Literal literal : literalData) {
					if (flag) {
						temp += " &";
					} else {
						flag = true;
					}
					if (literal.myTF[i] == 1)
						temp = temp + " " + literal.name;
					else
						temp = temp + " ~" + literal.name;
				}
				temp = temp + " )";
				result = result + " | " + temp;
			}
		}
		result = result.replace("  | (", " (");
		if (result.equals(" "))
			return "( " + literalData.get(0).name + " & ~" + literalData.get(0).name + " )" + "  *all false case";
		return result;
	}

}

class Literal {
	public Literal(String name, int[] myTF) {
		this.name = name;
		this.myTF = myTF;
	}

	String name;
	int[] myTF;

	void printMe() {
		System.out.println();
		System.out.print(name + " : ");
		for (int i : myTF) {
			if (i == 0)
				System.out.print("F ");
			else
				System.out.print("T ");
		}
		System.out.println();
	}

	public static Literal opHandler(Literal a, Literal b, Literal func) {
		if (func.name.equals("~"))
			return _not(a);
		if (func.name.equals("&"))
			return _and(a, b);
		if (func.name.equals("|"))
			return _or(a, b);
		if (func.name.equals("->"))
			return _eq(a, b);
		if (func.name.equals("<->"))
			return _deq(a, b);
		return null;
	}

	static Literal _and(Literal a, Literal b) {
		int[] temp = new int[a.myTF.length];
		for (int i = 0; i < a.myTF.length; i++) {
			temp[i] = a.myTF[i] * b.myTF[i];
		}
		return new Literal(a.name + "&" + b.name, temp);
	}

	static Literal _or(Literal a, Literal b) {
		int[] temp = new int[a.myTF.length];
		for (int i = 0; i < a.myTF.length; i++) {
			int t = a.myTF[i] + b.myTF[i];
			if (t == 2)
				t = 1;
			temp[i] = t;
		}
		return new Literal(a.name + "|" + b.name, temp);
	}

	static Literal _eq(Literal a, Literal b) {
		int[] temp = new int[a.myTF.length];
		for (int i = 0; i < a.myTF.length; i++) {
			int t1 = a.myTF[i];
			int t2 = b.myTF[i];
			int t = -1;
			if (t1 == 1 && t2 == 0)
				t = 0;
			else if (t1 == 1 && t2 == 1)
				t = 1;
			else if (t1 == 0 && t2 == 1)
				t = 1;
			else if (t1 == 0 && t2 == 0)
				t = 1;
			temp[i] = t;
		}
		return new Literal(a.name + "->" + b.name, temp);
	}

	static Literal _deq(Literal a, Literal b) {
		int[] temp = new int[a.myTF.length];
		for (int i = 0; i < a.myTF.length; i++) {
			int t1 = a.myTF[i];
			int t2 = b.myTF[i];
			int t = -1;
			if (t1 == 1 && t2 == 0)
				t = 0;
			else if (t1 == 1 && t2 == 1)
				t = 1;
			else if (t1 == 0 && t2 == 1)
				t = 0;
			else if (t1 == 0 && t2 == 0)
				t = 1;
			temp[i] = t;
		}
		return new Literal(a.name + "<->" + b.name, temp);
	}

	static Literal _not(Literal a) {
		int[] temp = new int[a.myTF.length];
		for (int i = 0; i < a.myTF.length; i++) {
			if (a.myTF[i] == 1)
				temp[i] = 0;
			else
				temp[i] = 1;
		}
		return new Literal("~" + a.name, temp);
	}
}
