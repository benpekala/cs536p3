import java.io.*;
import java.util.*;

// **********************************************************************
// The ASTnode class defines the nodes of the abstract-syntax tree that
// represents a Mini program.
//
// Internal nodes of the tree contain pointers to children, organized
// either in a list (for nodes that may have a variable number of 
// children) or as a fixed set of fields.
//
// The nodes for literals and ids contain line and character number
// information; for string literals and identifiers, they also contain a
// string; for integer literals, they also contain an integer value.
//
// Here are all the different kinds of AST nodes and what kinds of children
// they have.  All of these kinds of AST nodes are subclasses of "ASTnode".
// Indentation indicates further subclassing:
//
//     Subclass            Kids
//     --------            ----
//     ProgramNode         DeclListNode
//     DeclListNode        linked list of DeclNode
//     DeclNode:
//       VarDeclNode       TypeNode, IdNode, int
//       FnDeclNode        TypeNode, IdNode, FormalsListNode, FnBodyNode
//       FormalDeclNode    TypeNode, IdNode
//       StructDeclNode    IdNode, DeclListNode
//
//     FormalsListNode     linked list of FormalDeclNode
//     FnBodyNode          DeclListNode, StmtListNode
//     StmtListNode        linked list of StmtNode
//     ExpListNode         linked list of ExpNode
//
//     TypeNode:
//       IntNode           -- none --
//       BoolNode          -- none --
//       VoidNode          -- none --
//       StructNode        IdNode
//
//     StmtNode:
//       AssignStmtNode      AssignNode
//       PostIncStmtNode     ExpNode
//       PostDecStmtNode     ExpNode
//       ReadStmtNode        ExpNode
//       WriteStmtNode       ExpNode
//       IfStmtNode          ExpNode, DeclListNode, StmtListNode
//       IfElseStmtNode      ExpNode, DeclListNode, StmtListNode,
//                                    DeclListNode, StmtListNode
//       WhileStmtNode       ExpNode, DeclListNode, StmtListNode
//       RepeatStmtNode      ExpNode, DeclListNode, StmtListNode
//       CallStmtNode        CallExpNode
//       ReturnStmtNode      ExpNode
//
//     ExpNode:
//       IntLitNode          -- none --
//       StrLitNode          -- none --
//       TrueNode            -- none --
//       FalseNode           -- none --
//       IdNode              -- none --
//       DotAccessNode       ExpNode, IdNode
//       AssignNode          ExpNode, ExpNode
//       CallExpNode         IdNode, ExpListNode
//       UnaryExpNode        ExpNode
//         UnaryMinusNode
//         NotNode
//       BinaryExpNode       ExpNode ExpNode
//         PlusNode     
//         MinusNode
//         TimesNode
//         DivideNode
//         AndNode
//         OrNode
//         EqualsNode
//         NotEqualsNode
//         LessNode
//         GreaterNode
//         LessEqNode
//         GreaterEqNode
//
// Here are the different kinds of AST nodes again, organized according to
// whether they are leaves, internal nodes with linked lists of kids, or
// internal nodes with a fixed number of kids:
//
// (1) Leaf nodes:
//        IntNode,   BoolNode,  VoidNode,  IntLitNode,  StrLitNode,
//        TrueNode,  FalseNode, IdNode
//
// (2) Internal nodes with (possibly empty) linked lists of children:
//        DeclListNode, FormalsListNode, StmtListNode, ExpListNode
//
// (3) Internal nodes with fixed numbers of kids:
//        ProgramNode,     VarDeclNode,     FnDeclNode,     FormalDeclNode,
//        StructDeclNode,  FnBodyNode,      StructNode,     AssignStmtNode,
//        PostIncStmtNode, PostDecStmtNode, ReadStmtNode,   WriteStmtNode   
//        IfStmtNode,      IfElseStmtNode,  WhileStmtNode,  RepeatStmtNode,
//        CallStmtNode,    ReturnStmtNode,  DotAccessNode,   CallExpNode,
//        UnaryExpNode,    BinaryExpNode,   UnaryMinusNode, NotNode,
//        PlusNode,        MinusNode,       TimesNode,      DivideNode,
//        AndNode,         OrNode,          EqualsNode,     NotEqualsNode,
//        LessNode,        GreaterNode,     LessEqNode,     GreaterEqNode
//
// **********************************************************************

// **********************************************************************
// ASTnode class (base class for all other kinds of nodes)
// **********************************************************************

abstract class ASTnode { 
    // every subclass must provide an unparse operation
    abstract public void unparse(PrintWriter p, int indent);

    // this method can be used by the unparse methods to do indenting
    protected void printSpace(PrintWriter p, int indent) {
        for (int k=0; k<indent; k++) p.print(" ");
    }
}

// **********************************************************************
// ProgramNode,  DeclListNode, FormalsListNode, FnBodyNode,
// StmtListNode, ExpListNode
// **********************************************************************

class ProgramNode extends ASTnode {
    public ProgramNode(DeclListNode L) {
        declList = L;
    }

    public void unparse(PrintWriter p, int indent) {
        declList.unparse(p, indent);
    }

    // 1 kid
    private DeclListNode declList;
}

class DeclListNode extends ASTnode {
    public DeclListNode(List<DeclNode> S) {
        decls = S;
    }

    public void unparse(PrintWriter p, int indent) {
        Iterator it = decls.iterator();
        try {
            while (it.hasNext()) {
                ((DeclNode)it.next()).unparse(p, indent);
            }
        } catch (NoSuchElementException ex) {
            System.err.println("unexpected NoSuchElementException in DeclListNode.print");
            System.exit(-1);
        }
    }

    // list of kids (DeclNodes)
    private List<DeclNode> decls;
}

class FormalsListNode extends ASTnode {
    public FormalsListNode(List<FormalDeclNode> S) {
        formalDecls = S;
    }

    public void unparse(PrintWriter p, int indent) {
    }

    // list of kids (FormalDeclNodes)
    private List<FormalDeclNode> formalDecls;
}

class FnBodyNode extends ASTnode {
    public FnBodyNode(DeclListNode declList, StmtListNode stmtList) {
        this.declList = declList;
        this.stmtList = stmtList;
    }

    public void unparse(PrintWriter p, int indent) {
    }

    // 2 kids
    private DeclListNode declList;
    private StmtListNode stmtList;
}

class StmtListNode extends ASTnode {
    public StmtListNode(List<StmtNode> S) {
        stmts = S;
    }

    public void unparse(PrintWriter p, int indent) {
    }

    // list of kids (StmtNodes)
    private List<StmtNode> stmts;
}

class ExpListNode extends ASTnode {
    public ExpListNode(List<ExpNode> S) {
        exps = S;
    }

    public void unparse(PrintWriter p, int indent) {
    }

    // list of kids (ExpNodes)
    private List<ExpNode> exps;
}

// **********************************************************************
// DeclNode and its subclasses
// **********************************************************************

abstract class DeclNode extends ASTnode {
}

class VarDeclNode extends DeclNode {
    public VarDeclNode(TypeNode type, IdNode id, int size) {
        this.type = type;
        this.id = id;
        this.size = size;
    }

    public void unparse(PrintWriter p, int indent) {
        printSpace(p, indent);
        type.unparse(p, 0);
        p.print(" ");
        id.unparse(p, 0);
        p.println(";");
    }

    // 3 kids
    private TypeNode type;
    private IdNode id;
    private int size;  // use value NOT_STRUCT if this is not a struct type

    public static int NOT_STRUCT = -1;
}

class FnDeclNode extends DeclNode {
    public FnDeclNode(TypeNode type,
                      IdNode id,
                      FormalsListNode formalList,
                      FnBodyNode body) {
        this.type = type;
        this.id = id;
        formalsList = formalList;
        fnBody = body;
    }

    public void unparse(PrintWriter p, int indent) {
    }

    // 4 kids
    private TypeNode type;
    private IdNode id;
    private FormalsListNode formalsList;
    private FnBodyNode fnBody;
}

class FormalDeclNode extends DeclNode {
    public FormalDeclNode(TypeNode type, IdNode id) {
        this.type = type;
        this.id = id;
    }

    public void unparse(PrintWriter p, int indent) {
    }

    // 2 kids
    private TypeNode type;
    private IdNode id;
}

class StructDeclNode extends DeclNode {
    public StructDeclNode(IdNode id, DeclListNode declList) {
        this.id = id;
		this.declList = declList;
    }

    public void unparse(PrintWriter p, int indent) {
    }

    // 2 kids
    private IdNode id;
	private DeclListNode declList;
}

// **********************************************************************
// TypeNode and its Subclasses
// **********************************************************************

abstract class TypeNode extends ASTnode {
}

class IntNode extends TypeNode {
    public IntNode() {
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("int");
    }
}

class BoolNode extends TypeNode {
    public BoolNode() {
    }

    public void unparse(PrintWriter p, int indent) {
    }
}

class VoidNode extends TypeNode {
    public VoidNode() {
    }

    public void unparse(PrintWriter p, int indent) {
    }
}

class StructNode extends TypeNode {
    public StructNode(IdNode id) {
		this.id = id;
    }

    public void unparse(PrintWriter p, int indent) {
    }
	
	// 1 kid
    private IdNode id;
}

// **********************************************************************
// StmtNode and its subclasses
// **********************************************************************

abstract class StmtNode extends ASTnode {
}

class AssignStmtNode extends StmtNode {
    public AssignStmtNode(AssignNode assign) {
        this.assign = assign;
    }

    public void unparse(PrintWriter p, int indent) {
    }

    // 1 kid
    private AssignNode assign;
}

class PostIncStmtNode extends StmtNode {
    public PostIncStmtNode(ExpNode exp) {
        this.exp = exp;
    }

    public void unparse(PrintWriter p, int indent) {
    }

    // 1 kid
    private ExpNode exp;
}

class PostDecStmtNode extends StmtNode {
    public PostDecStmtNode(ExpNode exp) {
        this.exp = exp;
    }

    public void unparse(PrintWriter p, int indent) {
    }

    // 1 kid
    private ExpNode exp;
}

class ReadStmtNode extends StmtNode {
    public ReadStmtNode(ExpNode e) {
        exp = e;
    }

    public void unparse(PrintWriter p, int indent) {
    }

    // 1 kid (actually can only be an IdNode or an ArrayExpNode)
    private ExpNode exp;
}

class WriteStmtNode extends StmtNode {
    public WriteStmtNode(ExpNode exp) {
        this.exp = exp;
    }

    public void unparse(PrintWriter p, int indent) {
    }

    // 1 kid
    private ExpNode exp;
}

class IfStmtNode extends StmtNode {
    public IfStmtNode(ExpNode exp, DeclListNode dlist, StmtListNode slist) {
        declList = dlist;
        this.exp = exp;
        stmtList = slist;
    }

    public void unparse(PrintWriter p, int indent) {
    }

    // e kids
    private ExpNode exp;
    private DeclListNode declList;
    private StmtListNode stmtList;
}

class IfElseStmtNode extends StmtNode {
    public IfElseStmtNode(ExpNode exp, DeclListNode thenDeclList,
                          StmtListNode thenStmtList, DeclListNode elseDeclList,
                          StmtListNode elseStmtList) {
        this.exp = exp;
        this.thenDeclList = thenDeclList;
        this.thenStmtList = thenStmtList;
        this.elseDeclList = elseDeclList;
        this.elseStmtList = elseStmtList;
    }

    public void unparse(PrintWriter p, int indent) {
    }

    // 5 kids
    private ExpNode exp;
    private DeclListNode thenDeclList;
    private StmtListNode thenStmtList;
    private StmtListNode elseStmtList;
    private DeclListNode elseDeclList;
}

class WhileStmtNode extends StmtNode {
    public WhileStmtNode(ExpNode exp, DeclListNode dlist, StmtListNode slist) {
        this.exp = exp;
        declList = dlist;
        stmtList = slist;
    }
	
    public void unparse(PrintWriter p, int indent) {
    }

    // 3 kids
    private ExpNode exp;
    private DeclListNode declList;
    private StmtListNode stmtList;
}

class RepeatStmtNode extends StmtNode {
    public RepeatStmtNode(ExpNode exp, DeclListNode dlist, StmtListNode slist) {
        this.exp = exp;
        declList = dlist;
        stmtList = slist;
    }
	
    public void unparse(PrintWriter p, int indent) {
    }

    // 3 kids
    private ExpNode exp;
    private DeclListNode declList;
    private StmtListNode stmtList;
}

class CallStmtNode extends StmtNode {
    public CallStmtNode(CallExpNode call) {
        callExp = call;
    }

    public void unparse(PrintWriter p, int indent) {
    }

    // 1 kid
    private CallExpNode callExp;
}

class ReturnStmtNode extends StmtNode {
    public ReturnStmtNode(ExpNode exp) {
        this.exp = exp;
    }

    public void unparse(PrintWriter p, int indent) {
    }

    // 1 kid
    private ExpNode exp; // possibly null
}

// **********************************************************************
// ExpNode and its subclasses
// **********************************************************************

abstract class ExpNode extends ASTnode {
}

class IntLitNode extends ExpNode {
    public IntLitNode(int lineNum, int charNum, int intVal) {
        this.lineNum = lineNum;
        this.charNum = charNum;
        this.intVal = intVal;
    }

    public void unparse(PrintWriter p, int indent) {
    }

    private int lineNum;
    private int charNum;
    private int intVal;
}

class StringLitNode extends ExpNode {
    public StringLitNode(int lineNum, int charNum, String strVal) {
        this.lineNum = lineNum;
        this.charNum = charNum;
        this.strVal = strVal;
    }

    public void unparse(PrintWriter p, int indent) {
    }

    private int lineNum;
    private int charNum;
    private String strVal;
}

class TrueNode extends ExpNode {
    public TrueNode(int lineNum, int charNum) {
        this.lineNum = lineNum;
        this.charNum = charNum;
    }

    public void unparse(PrintWriter p, int indent) {
    }

    private int lineNum;
    private int charNum;
}

class FalseNode extends ExpNode {
    public FalseNode(int lineNum, int charNum) {
        this.lineNum = lineNum;
        this.charNum = charNum;
    }

    public void unparse(PrintWriter p, int indent) {
    }

    private int lineNum;
    private int charNum;
}

class IdNode extends ExpNode {
    public IdNode(int lineNum, int charNum, String strVal) {
        this.lineNum = lineNum;
        this.charNum = charNum;
        this.strVal = strVal;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print(strVal);
    }

    private int lineNum;
    private int charNum;
    private String strVal;
}

class DotAccessExpNode extends ExpNode {
    public DotAccessExpNode(ExpNode loc, IdNode id) {
        this.loc = loc;
        this.id = id;
    }

    public void unparse(PrintWriter p, int indent) {
    }

    // 2 kids
    private ExpNode loc;
    private IdNode id;
}

class AssignNode extends ExpNode {
    public AssignNode(ExpNode lhs, ExpNode exp) {
        this.lhs = lhs;
        this.exp = exp;
    }

    public void unparse(PrintWriter p, int indent) {
    }

    // 2 kids
    private ExpNode lhs;
    private ExpNode exp;
}

class CallExpNode extends ExpNode {
    public CallExpNode(IdNode name, ExpListNode elist) {
        id = name;
        expList = elist;
    }

    public CallExpNode(IdNode name) {
        id = name;
        expList = new ExpListNode(new LinkedList<ExpNode>());
    }

    // ** unparse **
    public void unparse(PrintWriter p, int indent) {
    }

    // 2 kids
    private IdNode id;
    private ExpListNode expList;  // possibly null
}

abstract class UnaryExpNode extends ExpNode {
    public UnaryExpNode(ExpNode exp) {
        this.exp = exp;
    }

    // one child
    protected ExpNode exp;
}

abstract class BinaryExpNode extends ExpNode {
    public BinaryExpNode(ExpNode exp1, ExpNode exp2) {
        this.exp1 = exp1;
        this.exp2 = exp2;
    }

    // two kids
    protected ExpNode exp1;
    protected ExpNode exp2;
}

// **********************************************************************
// Subclasses of UnaryExpNode
// **********************************************************************

class UnaryMinusNode extends UnaryExpNode {
    public UnaryMinusNode(ExpNode exp) {
        super(exp);
    }

    public void unparse(PrintWriter p, int indent) {
    }
}

class NotNode extends UnaryExpNode {
    public NotNode(ExpNode exp) {
        super(exp);
    }

    public void unparse(PrintWriter p, int indent) {
    }
}

// **********************************************************************
// Subclasses of BinaryExpNode
// **********************************************************************

class PlusNode extends BinaryExpNode {
    public PlusNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
    }
}

class MinusNode extends BinaryExpNode {
    public MinusNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
    }
}

class TimesNode extends BinaryExpNode {
    public TimesNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
    }
}

class DivideNode extends BinaryExpNode {
    public DivideNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
    }
}

class AndNode extends BinaryExpNode {
    public AndNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
    }
}

class OrNode extends BinaryExpNode {
    public OrNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
    }
}

class EqualsNode extends BinaryExpNode {
    public EqualsNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
    }
}

class NotEqualsNode extends BinaryExpNode {
    public NotEqualsNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
    }
}

class LessNode extends BinaryExpNode {
    public LessNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
    }
}

class GreaterNode extends BinaryExpNode {
    public GreaterNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
    }
}

class LessEqNode extends BinaryExpNode {
    public LessEqNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
    }
}

class GreaterEqNode extends BinaryExpNode {
    public GreaterEqNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
    }
}
