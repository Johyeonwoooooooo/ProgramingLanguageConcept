// AST.java
// AST for S
// 2022111967 조현우

import java.util.*;

class Indent {
    public static void display(int level, String s) {
        String tab = "";
        System.out.println();
        for (int i=0; i<level; i++)
            tab = tab + "  ";
        System.out.print(tab + s);
   }
} 

abstract class Command {
    // Command = Decl | Function | Stmt
    Type type =Type.UNDEF;
    public void display(int l) {  }
}

class Decls extends ArrayList<Decl> {
    // Decls = Decl*

    Decls() { super(); };
    Decls(Decl d) {
	    this.add(d);
    }
    // TODO: [Insert the code of display()]
	public void display(int level){  
		// Fill code herex
		Indent.display(level, "Decls");  // Decls 출력 후 level + 1
		level++;
		
		for(int i = 0; i<this.size(); i++) // Decls에 저장된 Decl을 출력
    	{
    		Decl d = this.get(i);  // Decls의 i 인덱스에 저장된 Decl 가져오기
    		d.display(level); // Decl 출력하기
    	}
		
	}    
}

class Decl extends Command {
    // Decl = Type type; Identifier id 
    Identifier id;
    Expr expr = null;
    int arraysize = 0;

    Decl (String s, Type t) {
        id = new Identifier(s); type = t;
    } // declaration 

    Decl (String s, Type t, int n) {
        id = new Identifier(s); type = t; arraysize = n;
    } // array declaration 

    Decl (String s, Type t, Expr e) {
        id = new Identifier(s); type = t; expr = e;
    } // declaration
   
    // TODO: [Insert the code of display()]
    public void display(int level)
    {
    	Indent.display(level, "Decl"); // Decl 출력후 level + 1
    	level++;
    	
    	type.display(level); // type display 하기
    	id.display(level);   // id   display 하기
    	if(expr != null)     // Decl시 expr이 있을 경우
    		expr.display(level);  // expr display하기
  
    }
}

class Functions extends ArrayList<Function> {
    // Functions = Function*
}

class Function extends Command  {
    // Function = Type type; Identifier id; Decls params; Stmt stmt
    Identifier id;
    Decls params;
    Stmt stmt;
   
    Function(String s, Type t) { 
        id = new Identifier(s); type = t; params = null; stmt = null;
    }

    public String toString ( ) { 
       return id.toString()+params.toString(); 
    }
}

class Type {
    // Type = int | bool | string | fun | array | except | void
    final static Type INT = new Type("int");
    final static Type BOOL = new Type("bool");
    final static Type STRING = new Type("string");
    final static Type VOID = new Type("void");
    final static Type FUN = new Type("fun");
    final static Type ARRAY = new Type("array");
    final static Type EXC = new Type("exc");
    final static Type RAISEDEXC = new Type("raisedexc");
    final static Type UNDEF = new Type("undef");
    final static Type ERROR = new Type("error");
    
    protected String id;
    protected Type(String s) { id = s; }
    public String toString ( ) { return id; }
    
    // TODO: [Insert the code of display()]
    public void display(int level)
    {
    	Indent.display(level, "Type: " + id.toString()); // Type: id (type value) 출력
    }
}

class ProtoType extends Type {
   // defines the type of a function and its parameters
   Type result;  
   Decls params;
   ProtoType (Type t, Decls ds) {
      super(t.id);
      result = t;
      params = ds;
   }
}

abstract class Stmt extends Command {
    // Stmt = Empty | Stmts | Assignment | If  | While | Let | Read | Print
}

class Empty extends Stmt {
    public void display (int level) {
        Indent.display(level, "Empty");
     }
}

class Stmts extends Stmt {
    // Stmts = Stmt*
    public ArrayList<Stmt> stmts = new ArrayList<Stmt>();
    
    Stmts() {
	    super(); 
    }

    Stmts(Stmt s) {
	     stmts.add(s);
    }
    
    // TODO: [Insert the code of display()]
    public void display(int level) 
    {
    	Indent.display(level, "Stmts"); // Stmts 출력후 level + 1
    	level++;
    	
    	for(int i = 0; i<stmts.size(); i++) // Stmts에 저장된 모든 stmt 출력
    	{
    		Stmt s = stmts.get(i);  // i번째 Stmts 원소 가져오기
    		s.display(level); // 해당 stmt 출력
    	}
    }
    
}

class Assignment extends Stmt {
    // Assignment = Identifier id; Expr expr
    Identifier id;
    Array ar = null;
    Expr expr;

    Assignment (Identifier t, Expr e) {
        id = t;
        expr = e;
    }

    Assignment (Array a, Expr e) {
        ar = a;
        expr = e;
    }
    
    // TODO: [Insert the code of display()]
    public void display(int level)
    {
    	Indent.display(level, "Assignment"); // Assignment 출력후 level + 1
    	level++;
    	
    	
    	id.display(level); // identifier display하기
    	if(ar != null)  // arr가 있을경우 display
    		ar.display(level);
    	expr.display(level); // expr display
    }
}

class If extends Stmt {
    // If = Expr expr; Stmt stmt1, stmt2;
    Expr expr;
    Stmt stmt1, stmt2;
    
    If (Expr t, Stmt tp) {
        expr = t; stmt1 = tp; stmt2 = new Empty( );
    }
    
    If (Expr t, Stmt tp, Stmt ep) {
        expr = t; stmt1 = tp; stmt2 = ep; 
    }
    
    // TODO: [Insert the code of display()]
    public void display(int level)
    {
    	Indent.display(level, "If"); // If 출력후 level + 1
    	level++;
    	
    	expr.display(level);  // expr display 
     	stmt1.display(level); // stmt1 display
    	stmt2.display(level); // stmt2 display
    }
}

class While extends Stmt {
    // While = Expr expr; Stmt stmt;
    Expr expr;
    Stmt stmt;

    While (Expr t, Stmt b) {
        expr = t; stmt = b;
    }
    
    // TODO: [Insert the code of display()]
    public void display(int level)
    {
    	Indent.display(level, "While"); // While 출력 후 level + 1
    	level++;
    	
    	expr.display(level); // expr display
    	stmt.display(level); // stmt display
    }
}

class Let extends Stmt {
    // Let = Decls decls; Functions funs; Stmts stmts; // <- Disregard [Functions funs]
    Decls decls;
    Functions funs;
    Stmts stmts;

    Let(Decls ds, Stmts ss) {
        decls = ds;
		funs = null;
        stmts = ss;
    }

    Let(Decls ds, Functions fs, Stmts ss) {
        decls = ds;
	    funs = fs;
        stmts = ss;
    }
    
    // TODO: [Insert the code of display()]
    public void display(int level)
    {
    	Indent.display(level, "Let");  // Let 출력 후 level + 1
    	level++;
    	
    	decls.display(level); // decls 출력하기
    	stmts.display(level); // stmts 출력하기
    }
}

class Read extends Stmt {
    // Read = Identifier id
    Identifier id;

    Read (Identifier v) {
        id = v;
    }
    
    // TODO: [Insert the code of display()]
    public void display(int level)
    {
    	Indent.display(level, "Read"); // Read 출력 후 level + 1
    	level++;
    	
    	id.display(level); // identifier display
    }
}

class Print extends Stmt {
    // Print =  Expr expr
    Expr expr;

    Print (Expr e) {
        expr = e;
    }
    
    // TODO: [Insert the code of display()]
    public void display(int level)
    {
    	Indent.display(level,"Print"); // Print 출력 후 level + 1
    	level++;
    	
    	expr.display(level); // expr display
    }
}

class Return extends Stmt {
    Identifier fid;
    Expr expr;

    Return (String s, Expr e) {
        fid = new Identifier(s);
        expr = e;
    }
}

class Try extends Stmt {
    // Try = Identifier id; Stmt stmt1; Stmt stmt2; 
    Identifier eid;
    Stmt stmt1; 
    Stmt stmt2; 

    Try(Identifier id, Stmt s1, Stmt s2) {
        eid = id; 
        stmt1 = s1;
        stmt2 = s2;
    }
}

class Raise extends Stmt {
    Identifier eid;

    Raise(Identifier id) {
        eid = id;
    }
}

class Exprs extends ArrayList<Expr> {
    // Exprs = Expr*
}

abstract class Expr extends Stmt {
    // Expr = Identifier | Value | Binary | Unary | Call

}

class Call extends Expr { 
    Identifier fid;  
    Exprs args;

    Call(Identifier id, Exprs a) {
       fid = id;
       args = a;
    }
}

class Identifier extends Expr {
    // Identifier = String id
    private String id;

    Identifier(String s) { id = s; }

    public String toString( ) { return id; }
    
    public boolean equals (Object obj) {
        String s = ((Identifier) obj).id;
        return id.equals(s);
    }
    
    // TODO: [Insert the code of display()]
    public void display(int level)
    {
    	Indent.display(level, "Identifier: " + id.toString()); // Identifier: id_name 출력하기
    }
}

class Array extends Expr {
    // Array = Identifier id; Expr expr
    Identifier id;
    Expr expr = null;

    Array(Identifier s, Expr e) {id = s; expr = e;}

    public String toString( ) { return id.toString(); }
    
    public boolean equals (Object obj) {
        String s = ((Array) obj).id.toString();
        return id.equals(s);
    }
}

class Value extends Expr 
{
    // Value = int | bool | string | array | function 
    protected boolean undef = true;
    Object value = null; // Type type;
    
    Value(Type t) {
        type = t;  
        if (type == Type.INT) value = new Integer(0);
        if (type == Type.BOOL) value = new Boolean(false);
        if (type == Type.STRING) value = "";
        undef = false;
    }

    Value(Object v) {
        if (v instanceof Integer) type = Type.INT;
        if (v instanceof Boolean) type = Type.BOOL;
        if (v instanceof String) type = Type.STRING;
        if (v instanceof Function) type = Type.FUN;
        if (v instanceof Value[]) type = Type.ARRAY;
        value = v; undef = false; 
    }

    Object value() { return value; }

    int intValue( ) { 
        if (value instanceof Integer) 
            return ((Integer) value).intValue(); 
        else return 0;
    }
    
    boolean boolValue( ) { 
        if (value instanceof Boolean) 
            return ((Boolean) value).booleanValue(); 
        else return false;
    } 

    String stringValue ( ) {
        if (value instanceof String) 
            return (String) value; 
        else return "";
    }

    Function funValue ( ) {
        if (value instanceof Function) 
            return (Function) value; 
        else return null;
    }

    Value[] arrValue ( ) {
        if (value instanceof Value[]) 
            return (Value[]) value; 
        else return null;
    }

    Type type ( ) { return type; }

    public String toString( ) {
        //if (undef) return "undef";
        if (type == Type.INT) return "" + intValue(); 
        if (type == Type.BOOL) return "" + boolValue();
	    if (type == Type.STRING) return "" + stringValue();
        if (type == Type.FUN) return "" + funValue();
        if (type == Type.ARRAY) return "" + arrValue();
        return "undef";
    }
    
    // TODO: [Insert the code of display()]
    public void display(int level)
    {
    	Indent.display(level, "Value: " + this.toString()); // Value: (value) 출력하기 
    }
}

class Binary extends Expr {
// Binary = Operator op; Expr expr1; Expr expr2;
    Operator op;
    Expr expr1, expr2;

    Binary (Operator o, Expr e1, Expr e2) 
    {
        op = o; expr1 = e1; expr2 = e2;
    } // binary
    
    // TODO: [Insert the code of display()]
    public void display(int level)
    {
    	Indent.display(level, "Binary"); // Binary 출력 후 level + 1
    	level++;
    	
    	op.display(level);  // op display
    	expr1.display(level); // expr1 display
    	expr2.display(level); // expr2 display
    }
}

class Unary extends Expr {
    // Unary = Operator op; Expr expr
    Operator op;
    Expr expr;

    Unary (Operator o, Expr e) {
        op = o; //(o.val == "-") ? new Operator("neg"): o; 
        expr = e;
    } // unary
    
    // TODO: [Insert the code of display()]
    public void display(int level)
    {
    	Indent.display(level, "Unary"); // Unary 출력 후 level + 1
    	level++;
    	
    	op.display(level); // op display
    	expr.display(level); // expr display
    }

}

class Operator {
    String val;
    
    Operator (String s) { 
	val = s; 
    }

    public String toString( ) { 
	return val; 
    }

    public boolean equals(Object obj) { 
	return val.equals(obj); 
    }
    
    // TODO: [Insert the code of display()]
    public void display(int level)
    {
    	Indent.display(level, "Operator: " + this.toString()); // operator: (op_value) 출력하기
    }
}