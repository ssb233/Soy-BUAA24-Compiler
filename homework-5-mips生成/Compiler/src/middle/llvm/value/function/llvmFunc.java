package middle.llvm.value.function;

import middle.llvm.llvmModule;
import middle.llvm.llvmOutput;
import middle.llvm.llvmUser;
import middle.llvm.type.llvmArrayType;
import middle.llvm.type.llvmFuncType;
import middle.llvm.type.llvmIntegerType;
import middle.llvm.type.llvmType;
import middle.llvm.value.basicblock.llvmBasicBlock;
import middle.llvm.value.instructions.llvmInstruction;

import java.util.ArrayList;

public class llvmFunc extends llvmUser implements llvmOutput {
    private ArrayList<llvmParam> params = new ArrayList<llvmParam>();
    private ArrayList<llvmBasicBlock> blocks = new ArrayList<>();
    private ArrayList<llvmInstruction> instrutions = new ArrayList<llvmInstruction>();
    private llvmModule module;
    private llvmFuncCnt funcCnt;

    public llvmFunc(llvmType type) {
        super(type);
    }
    public llvmFunc(llvmType type,ArrayList<llvmParam> params,
                    String name,llvmModule module,
                    llvmFuncCnt funcCnt){
        super(type,name);
        this.params = params;
        this.module = module;
        this.funcCnt = funcCnt;

    }
    public llvmFunc(llvmType type,String name,llvmModule module,llvmFuncCnt funcCnt){
        super(type,name);
        //params不动了，就是空列表
        this.module = module;
        this.funcCnt = funcCnt;
    }

    public void addBasicBlock(llvmBasicBlock basicBlock){
        this.blocks.add(basicBlock);
    }
    public ArrayList<llvmInstruction> getInstrutions(){
        return this.instrutions;
    }

    public String llvmOutput(){
        StringBuilder sb = new StringBuilder();
        sb.append("define dso_local ");
        if(this.getValueType() instanceof llvmFuncType){
            sb.append(((llvmFuncType) this.getValueType()).getRetType().llvmOutput());
        }
        sb.append(" @"+this.getName()+"(");
        int num = 0;
        if(params!=null){
            for(llvmParam item:params){
                if(item.getValueType() instanceof llvmIntegerType){
                    sb.append(item.getValueType().llvmOutput());
                    sb.append(" ");
                }else if(item.getValueType() instanceof llvmArrayType){
                    llvmType type = ((llvmArrayType) item.getValueType()).getElementType();
                    sb.append(type.llvmOutput()+"* ");
                }
                sb.append(item.getName()+",");
                num++;
            }
            if(params.size()>0) sb.deleteCharAt(sb.length()-1);
        }

        sb.append(")");
        sb.append("{\n");
//        for(llvmBasicBlock item:blocks){
//            sb.append(item.llvmOutput());
//        }
        for(llvmInstruction ins:instrutions){
            sb.append(ins.llvmOutput());
        }
        sb.append("}\n");
        return sb.toString();
    }

    public void setAllName(){
        if(this.params!=null){
            for(llvmParam param:this.params){
                param.setAllName(this.funcCnt);
            }
        }
//        if(this.blocks!=null){
//            for(llvmBasicBlock basicBlock:this.blocks){
//                basicBlock.setAllName(this.funcCnt);
//            }
//        }
        if(this.instrutions!=null){
            for(llvmInstruction ins:this.instrutions){
                ins.setAllName(funcCnt,this.getName());
            }
        }
    }

    public void removeIns(){
        llvmInstruction curIns = null;
        for(llvmBasicBlock block:this.blocks){
            for(llvmInstruction ins:block.getInstructions()){
                if(curIns==null){
                    this.instrutions.add(ins);
                    if(ins.getInstructionType().toString().equals("ret")||ins.getInstructionType().toString().equals("br")){
                        curIns = ins;
                    }
                }else{//此时标记了找到的ret指令，那么直到遇到标签才去除标记
                    if(ins.getInstructionType().toString().equals("Label")){
                        this.instrutions.add(ins);//标签得加入
                        curIns = null;
                    }
                }

            }
        }
    }

    public ArrayList<llvmParam> getParams(){
       ArrayList<llvmParam> list = new ArrayList<>();
       if(this.params!=null){
           for(llvmParam item:this.params){
               list.add(item);
           }
       }
       return list;
    }
    public ArrayList<llvmBasicBlock> getBlocks(){
        ArrayList<llvmBasicBlock> list = new ArrayList<>();
        if(this.blocks!=null){
            for(llvmBasicBlock item:this.blocks){
                list.add(item);
            }
        }
        return list;
    }
}
