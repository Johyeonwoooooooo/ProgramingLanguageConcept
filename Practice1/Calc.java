// 2022111967 조현우 프로그래밍언어개념 LAB1
import java.io.*;

class Calc {
    int token; int value; int ch; 
    private PushbackInputStream input;
    final int NUMBER=256;

    Calc(PushbackInputStream is) { // 생성자
        input = is;
    }

    int getToken( )  { /* tokens are characters */
        while(true) {
            try  {
	            ch = input.read();
                if (ch == ' ' || ch == '\t' || ch == '\r') ;
                else 
                    if (Character.isDigit(ch)) {
                        value = number( );	
	               input.unread(ch);
		     return NUMBER;
	          }
	          else return ch;
	  } catch (IOException e) {
                System.err.println(e);
            }
        }
    }

    private int number( )  {
    /* number -> digit { digit } */
        int result = ch - '0';
        try  {
            ch = input.read();
            while (Character.isDigit(ch)) {
                result = 10 * result + ch -'0';
                ch = input.read(); 
            }
        } catch (IOException e) {
            System.err.println(e);
        }
        return result;
    }

    void error( ) {
        System.out.printf("parse error : %d\n", ch);
        //System.exit(1);&
    }

    void match(int c) { 
        if (token == c) 
        	token = getToken();
        else 
        	error();
    }

    void command( ) {
    /* command -> expr '\n' */
        // int result = aexp();		// TODO: [Remove this line!!]	
        Object result = expr();  // TODO: [Use this line for solution]
        if (token == '\n') /* end the parse and print the result */
        	System.out.println(result);
        else 
        	error();
    }
    
    Object expr() {
    /* <expr> -> <bexp> {& <bexp> | '|'<bexp>} | !<expr> | true | false */
    	Object result;
    	// result = ""; // TODO: [Remove this line!!]
    	if (token == '!')
    	{
    		// !<expr>
    		match('!');
    		result = !(boolean) expr();
    	}
    	else if (token == 't')
    	{
    		// true
    		match('t');
    		result = (boolean)true;
    	}
    	else if (token == 'f')
    	{
    		// false
    		// TODO: [Fill in your code here]
    		match('f');                  // f가 맞는지 검사 후 다음 토큰 갱신
    		result = (boolean)false;     // result 를 boolean false로 설정
    	}
    	else 
    	{
    		/* <bexp> {& <bexp> | '|'<bexp>} */
    		result = bexp();
    		while (token == '&' || token == '|') 
    		{
    			if (token == '&')
    			{
    				// TODO: [Fill in your code here]
    				match('&');              // & 가 맞는지 검사 후 다음 토큰 갱신
    				result = (boolean)result & (boolean) bexp();  // bexp 와 AND 연산을 result로 설정 
    			}
    			else if (token == '|')
    			{
    				// TODO: [Fill in your code here]
    				match('|');            // | 가 맞는지 확인 후 다음 토큰 갱신
    				result = (boolean)result | (boolean) bexp();  // bexp 와 OR 연산을 result로 설정
    			}
    		}
    	}
    	return result;
	}

    Object bexp( ) {
    /* <bexp> -> <aexp> [<relop> <aexp>] */
    	Object result;
    	// result = ""; // TODO: [Remove this line!!]
    	int aexp1 = aexp();  // 첫 피연산자 aexp
    	if (token == '<' || token == '>' || token == '=' || token == '!')
    	{ // <relop>
    		/* Check each string using relop(): "<", "<=", ">", ">=", "==", "!=" */
    		// TODO: [Fill in your code here]
    		result = relop(); // 비교 연산 기호 가져오기
            int aexp2 = aexp(); // 두번째 피연산자 aexp 
            if (result.equals("<"))  // 해당 연산기호와 맞는지 string 비교
            {
                result = (boolean)(aexp1 < aexp2);  // aexp1, aexp2를 비교연산 후 결과를 boolean 값으로 저장
            } 
            else if (result.equals("<=")) // 동일
            {
                result = (boolean)(aexp1 <= aexp2);
            } 
            else if (result.equals(">")) 
            {
                result = (boolean)(aexp1 > aexp2);
            } 
            else if (result.equals(">=")) 
            {
                result = (boolean)(aexp1 >= aexp2);
            } 
            else if (result.equals("==")) 
            {
                result = (boolean)(aexp1 == aexp2);
            } 
            else if (result.equals("!="))
            {
                result = (boolean)(aexp1 != aexp2);
            }
    	}
		else 
		{
			result = aexp1;
		}
    	return result;		
	}

    String relop() {    	
    /* <relop> -> ( < | <= | > | >= | == | != ) */    	
    	String result = ""; 
    	// TODO: [Fill in your code here]
    	 if (token == '<') 
    	 { // 토큰 검사하기 
    	    match('<');  // 각 토큰이 맞는지 확인  
    	    result = "<";
    	    if(token == '=')  // 토큰은 하나씩 검사하기때문에 다음 토큰이 = 이면 <= 을 처리하게 함
    	    {
    	       	match('='); // = 검사 후 다음 토큰
    	       	result = ">="; // result 만들기
    	    }
    	 } 
    	 else if (token == '>') 
    	 {
    	     match('>');
    	     result = ">";
    	     if(token == '=')  // 위 주석과 동일
    	     {
    	       	match('=');
    	       	result = ">=";
    	     }
    	 }
    	 else if (token == '=') 
    	 {
    	     match('=');
    	     result = "=";
    	     if(token == '=')
    	     {
    	    	 match('=');
    	     	result = "==";
    	     }
    	 }
    	 else if (token == '!') 
    	 {
    	     match('!');
    	     if(token == '=')
    	     {
    	    	 match('=');
    	    	 result = "!=";
    	     }
    	 }
    	return result;
	}
    
    // TODO: [Modify code of aexp() for <aexp> -> <term> { + <term> | - <term> }]
    int aexp() {
    /* expr -> term { '+' term } */
        int result = term();
        while (token == '+' || token == '-') {  // aexp의 - 추가
            if(token == '+') 
            {
                match('+');
                result += term();
            }
            else  // - 일때
            { 
                match('-');  // - 인지 확인하고 
                result -= term();  // result 에 - 하기
            }
        }
        return result;
    }

 // TODO: [Modify code of term() for <term> -> <factor> { * <factor> | / <factor>}]
    int term( ) {
    /* term -> factor { '*' factor } */
       int result = factor();
       while (token == '*' || token == '/') 
       {  // term 에 / 추가
           if(token =='*') 
           {
               match('*');
               result *= factor();
           }
           else 
           {
               match('/');    // / 인지 확인
               result /= factor();  // result 를 / 연산
           }
       }
       return result;
    }

    int factor() {
    /* factor -> '(' expr ')' | number */
        int result = 0;
        if (token == '(') {
            match('(');
            result = aexp();
            match(')');
        }
        else if (token == NUMBER) {
            result = value;
	        match(NUMBER); //token = getToken();
        }
        return result;
    }

    void parse( ) {
        token = getToken(); // get the first token
        command();          // call the parsing command
    }

    public static void main(String args[]) { 
        Calc calc = new Calc(new PushbackInputStream(System.in));
        while(true) {
            System.out.print(">> ");
            calc.parse();
        }
    }
}