 // Sint.java
// Interpreter for S
import java.util.Scanner;

public class Sint {
    static Scanner sc = new Scanner(System.in);
    static State state = new State();

    State Eval(Command c, State state) { 
	    if (c instanceof Decl) {
	        Decls decls = new Decls();
	        decls.add((Decl) c);
	        return allocate(decls, state);
	    }

	    if (c instanceof Function) { 
	        Function f = (Function) c; 
	        state.push(f.id, new Value(f)); 
	        return state;
	    }

	    if (c instanceof Stmt)
	        return Eval((Stmt) c, state); 
		
	    throw new IllegalArgumentException("no command");
    }

    State Eval(Stmt s, State state) {
        if (s instanceof Empty) 
	        return Eval((Empty)s, state);
        if (s instanceof Assignment)  
	        return Eval((Assignment)s, state);
        if (s instanceof If)  
	        return Eval((If)s, state);
        if (s instanceof While)  
	        return Eval((While)s, state);
        if (s instanceof Stmts)  
	        return Eval((Stmts)s, state);
	    if (s instanceof Let)  
	        return Eval((Let)s, state);
	    if (s instanceof Read)  
	        return Eval((Read)s, state);
	    if (s instanceof Print)  
	        return Eval((Print)s, state);
        if (s instanceof Call) 
	        return Eval((Call)s, state);
	    if (s instanceof Return) 
	        return Eval((Return)s, state);
        throw new IllegalArgumentException("no statement");
    }

    // [Function]
    // call without return value
    State Eval(Call c, State state) {
    	// TODO: [Fill the code of call stmt]
	     Value v = state.get(c.fid); //  호출문의 identfier와 맞는 value를 state에서 가져옴
         Function f = v.funValue();  // 그 value 객체를 function vlaue로 바꿈
         state = newFrame(state, c, f); // 새로운 프레임을 생성하고 state를 업데이트함
         state = Eval(f.stmt, state);  // function의 내용인 stmt를 실행하고 state를 업데이트함
         state = deleteFrame(state, c, f);  // 생성한 프레임을 삭제하고 state를 업데이트함
        
	    return state; // 모든 작업이 끝난 state를 반환
    }
    
    // [Function]
    // value-returning call 
    Value V (Call c, State state) { 
	    Value v = state.get(c.fid);  	 	// find function
        Function f = v.funValue();
        state = newFrame(state, c, f); 		// create the frame on the stack
        state = Eval(f.stmt, state); 	   	// interpret the call
	    v = state.peek().val;				// get the return value
        state = deleteFrame(state, c, f); 	// delete the frame from the stack
        return v;
    }
    
    // [Function]
    State Eval(Return r, State state) {
        Value v = V(r.expr, state);
        return state.set(new Identifier("return"), v); 
    }

    // [Function]
    State newFrame (State state, Call c, Function f) {
        if (c.args.size() == 0) 
            return state;
        
        // TODO: [Fill the code of newFrame]
    	// evaluate arguments
         Value val[] = new Value[c.args.size()]; // call 객체가 가지는 exprs의 크기만큼 Value 객체를 생성
         int i=0; // value araray의 index
         for (Expr e : c.args) // exprs의 모든 expr을 순회
            val[i++] = V(e,state);  // value 객체 배열에 모든 expr를 value로 생성함, index++을 하여 할당 후 index + 1함
        
	    // activate a new stack frame in the stack
         i=0; // value array의 index
         for (Decl d : f.params) { // pass by value ( function에 저장된 모든 파라미터 )
            Identifier v = (Identifier)d.id; // decl의 id를 가져옴
            state.push(v, val[i++]);  // decl의 id와 해당 value 객체를 state에 저장함
         }
        
        state.push(new Identifier("return"), null); // allocate for return value
        return state;
    }

    // [Function]
    State deleteFrame (State state, Call c, Function f) {
    // free a stack frame from the stack
        state.pop();  // pop the return value	
        
        // TODO: [Fill the code of deleteFrame]
         if (f.params != null)  // function의 파라미터가 있으면
            state  = free(f.params, state); // state에 있는 function의 파라미터를 삭제함
       
        return state; // 모든 프레임을 삭제한 후 리턴함
    }

    State Eval(Empty s, State state) {
        return state;
    }
  
    State Eval(Assignment a, State state) {
        Value v = V(a.expr, state);

        if (a.ar == null)
	        return state.set(a.id, v);
        else {
            Value i = V(a.ar.expr, state);
            Value val = state.get(a.ar.id);
            Value[] ar = (Value[]) val.arrValue();
            ar[i.intValue()] = v;
            return state;
        }
    }

    State Eval(Read r, State state) {
        if (r.id.type == Type.INT) {
	        int i = sc.nextInt();
	        state.set(r.id, new Value(i));
	    } 
 
	    if (r.id.type == Type.BOOL) {
	        boolean b = sc.nextBoolean();	
            state.set(r.id, new Value(b));
	    }

	    if (r.id.type == Type.STRING) {
	        String s = (String) sc.next();
	        state.set(r.id, new Value(s));
	    } 
	    return state;
    }

    State Eval(Print p, State state) {
	    System.out.println(V(p.expr, state));
        return state; 
    }
  
    State Eval(Stmts ss, State state) {
        for (Stmt s : ss.stmts) {
            state = Eval(s, state);
            if (s instanceof Return)  
                return state;
        }
        return state;
    }
  
    State Eval(If c, State state) {
        if (V(c.expr, state).boolValue( ))
            return Eval(c.stmt1, state);
        else
            return Eval(c.stmt2, state);
    }
 
    State Eval(While l, State state) {
        if (V(l.expr, state).boolValue( ))
            return Eval(l, Eval(l.stmt, state));
        else 
	        return state;
    }

    State Eval(Let l, State state) {
        State s = allocate(l.decls, state);
        
        // [Function]
        if (l.funs != null) {
            // TODO: [Fill the code here]
            // Case with Function declaration inside Let
        	 for (Function fun : l.funs)    // let문에 저장된 function을 모두 순회함
        		s.push(fun.id, new Value(fun)); // state에 function의 id와 func값을 나타내는 Value 객체를 저장함.
        }
        
        s = Eval(l.stmts, s); // let문에 저장된 모든 stmts를 호출한 후 state에 저장

	    return free(l.decls, s);
    }

    State allocate (Decls ds, State state) {
        if (ds != null)
        for (Decl decl : ds) 
		    if (decl.arraysize > 0) { 
                Value[] ar = new Value[decl.arraysize];
                state.push(decl.id, new Value(ar)); 
            }
            else if (decl.expr == null)
                state.push(decl.id, new Value(decl.type));
	        else
		        state.push(decl.id, V(decl.expr, state));

        return state;
    }

    State free (Decls ds, State state) {
        if (ds != null)
        for (Decl decl : ds) 
            state.pop();

        return state;
    }

    Value binaryOperation(Operator op, Value v1, Value v2) {
        check(!v1.undef && !v2.undef,"reference to undef value");
	    switch (op.val) {
	    case "+":
            return new Value(v1.intValue() + v2.intValue());
        case "-": 
            return new Value(v1.intValue() - v2.intValue());
        case "*": 
            return new Value(v1.intValue() * v2.intValue());
        case "/": 
            return new Value(v1.intValue() / v2.intValue());
        case "==":
        	if (v1.type == Type.STRING)
        		return new Value(v1.value == v2.value);
        	else if (v1.type == Type.BOOL)
        		return new Value(v1.boolValue() == v2.boolValue());
        	else
        		return new Value(v1.intValue() == v2.intValue());
        case "!=":
        	if (v1.type == Type.STRING)
        		return new Value(v1.value != v2.value);
        	else if (v1.type == Type.BOOL)
        		return new Value(v1.boolValue() != v2.boolValue());
        	else
        		return new Value(v1.intValue() != v2.intValue());
        case "<":
        	if (v1.type == Type.STRING) {
            	String v1_str = (String)v1.value;
            	String v2_str = (String)v2.value;
            	int res = v1_str.compareTo(v2_str);
            	boolean res_value;
            	if (res < 0) res_value = true;
            	else res_value = false;
            	return new Value(res_value);
        	}
        	else
        		return new Value(v1.intValue() < v2.intValue());
        case "<=":
        	if (v1.type == Type.STRING) {
            	String v1_str = (String)v1.value;
            	String v2_str = (String)v2.value;
            	int res = v1_str.compareTo(v2_str);
            	boolean res_value;
            	if (res <= 0) res_value = true;
            	else res_value = false;
            	return new Value(res_value);
        	}
        	else
        		return new Value(v1.intValue() <= v2.intValue());
        case ">":
        	if (v1.type == Type.STRING) {
            	String v1_str = (String)v1.value;
            	String v2_str = (String)v2.value;
            	int res = v1_str.compareTo(v2_str);
            	boolean res_value;
            	if (res > 0) res_value = true;
            	else res_value = false;
            	return new Value(res_value);
        	}
        	else
        		return new Value(v1.intValue() > v2.intValue());
        case ">=":
        	if (v1.type == Type.STRING) {
            	String v1_str = (String)v1.value;
            	String v2_str = (String)v2.value;
            	int res = v1_str.compareTo(v2_str);
            	boolean res_value;
            	if (res >= 0) res_value = true;
            	else res_value = false;
            	return new Value(res_value);
        	}
        	else
        		return new Value(v1.intValue() >= v2.intValue());
        case "&": 
            return new Value(v1.boolValue() && v2.boolValue());
        case "|": 
            return new Value(v1.boolValue() || v2.boolValue());            

	    default:
	        throw new IllegalArgumentException("no operation");
	    }
    } 
    
    Value unaryOperation(Operator op, Value v) {
        check( !v.undef, "reference to undef value");
	    switch (op.val) {
        case "!": 
            return new Value(!v.boolValue( ));
	    case "-": 
            return new Value(-v.intValue( ));
        default:
            throw new IllegalArgumentException("no operation: " + op.val); 
        }
    } 

    static void check(boolean test, String msg) {
        if (test) return;
        System.err.println(msg);
    }

    Value V(Expr e, State state) {
        if (e instanceof Value) 
            return (Value) e;
        if (e instanceof Identifier) {
	        Identifier v = (Identifier) e;
            return (Value)(state.get(v));
	    }
        if (e instanceof Array) {
	        Array ar = (Array) e;
            Value i = V(ar.expr, state);
            Value v = (Value) state.get(ar.id);
            Value[] vs = v.arrValue(); 
            return (vs[i.intValue()]); 
	    }
        if (e instanceof Binary) {
            Binary b = (Binary) e;
            Value v1 = V(b.expr1, state);
            Value v2 = V(b.expr2, state);
            return binaryOperation (b.op, v1, v2); 
        }
        if (e instanceof Unary) {
            Unary u = (Unary) e;
            Value v = V(u.expr, state);
            return unaryOperation(u.op, v); 
        }
        if (e instanceof Call) 
    	    return V((Call)e, state);  
        throw new IllegalArgumentException("no operation");
    }

    public static void main(String args[]) {
	    if (args.length == 0) {
	        Sint sint = new Sint(); 
			Lexer.interactive = true;
            System.out.println("Language S Interpreter 1.0");
            System.out.print(">> ");
	        Parser parser  = new Parser(new Lexer());

	        do { // Program = Command*
	            if (parser.token == Token.EOF)
		            parser.token = parser.lexer.getToken();
	       
	            Command command=null;
                try {
	                command = parser.command();
	                 //    if (command != null) command.display(0);    // display AST    
					 if (command == null) 
						 throw new Exception();
					 else  { 
						 command.type = TypeChecker.Check(command); 
//						  System.out.println("\nType: "+ command.type); 
				     }
                } catch (Exception e) {
                    System.out.println("Error: " + e);
		            System.out.print(">> ");
                    continue;
                }

	            if (command.type != Type.ERROR) {
                    System.out.println("\nInterpreting..." );
                    try {
                        state = sint.Eval(command, state);
                    } catch (Exception e) {
                         System.err.println("Error: " + e);  
                    }
                }
		        System.out.print(">> ");
	        } while (true);
	    }
        else {
	        System.out.println("Begin parsing... " + args[0]);
	        Command command = null;
	        Parser parser  = new Parser(new Lexer(args[0]));
	        Sint sint = new Sint();

	        do {	// Program = Command*
	            if (parser.token == Token.EOF)
                    break;
	         
                try {
		            command = parser.command();
					//  if (command != null) command.display(0);      // display AST
					if (command == null) 
						 throw new Exception();
					else  {
						 command.type = TypeChecker.Check(command); 
//                         System.out.println("\nType: "+ command.type);  
				    }
                } catch (Exception e) {
                    System.out.println("Error: " + e);
                    continue;
                }

	            if (command.type!=Type.ERROR) {
                    System.out.println("\nInterpreting..." + args[0]);
                    try {
                        state = sint.Eval(command, state);
                    } catch (Exception e) {
                        System.err.println("Error: " + e);  
                    }
                }
	        } while (command != null);
        }        
    }
}