package com.shine.integrationtestcover.regressiontest;

import jdk.internal.org.objectweb.asm.tree.*;

import java.util.Arrays;

/**
 * @Author: Shine
 * @Date: 2019/5/11
 */
public class CompareUtil {
    public static boolean compareFieldInsnode(FieldInsnNode a, FieldInsnNode b) {
        if((a.owner.equals(b.owner))&&(a.desc.equals(b.desc))&&(a.name.equals(b.name))) {
            //field change
            return true;
        } else {
            return false;
        }

    }

    public static boolean compareFrameNode(FrameNode a, FrameNode b) {
        return (Arrays.equals(a.local.toArray(), b.local.toArray())) && (Arrays.equals(a.stack.toArray(), b.stack.toArray()));
    }

    public static boolean compareIincInsnNode(IincInsnNode a, IincInsnNode b) {
        return (a.var == b.var) && (a.incr == b.incr);
    }

    public static boolean compareInsnNode(InsnNode a, InsnNode b) {
        return (a.getOpcode() == b.getOpcode());
    }

    public static boolean compareIntInsnNode(IntInsnNode a, IntInsnNode b) {
        return (a.operand == b.operand);
    }

    public static boolean compareInvokeDynamicInsnNode(InvokeDynamicInsnNode a, InvokeDynamicInsnNode b) {
        return (a.name.equals(b.name))&&(a.desc.equals(b.desc))&&(a.bsm.equals(b.bsm))&&(Arrays.equals(a.bsmArgs, b.bsmArgs));
    }

    public static boolean compareJumpInsnNode(JumpInsnNode a, JumpInsnNode b) {
        return (a.getOpcode()==b.getOpcode());
    }

    public static boolean compareLabelNode(LabelNode a, LabelNode b) {
        return (a.getLabel().toString().equals(b.getLabel().toString()));
    }

    public static boolean compareLdcInsnNode(LdcInsnNode a, LdcInsnNode b) {
        return (a.cst.equals(b.cst));
    }

    public static boolean compareLineNumberNode(LineNumberNode a, LineNumberNode b) {
        return (a.start.getLabel().toString().equals(b.start.getLabel().toString()))&&(a.line == b.line);
    }

    public static boolean compareLookupSwitchInsnNode(LookupSwitchInsnNode a, LookupSwitchInsnNode b) {
        return (a.dflt.getLabel().toString().equals(b.dflt.getLabel().toString()))&&(Arrays.equals(a.keys.toArray(), b.keys.toArray()))&&(Arrays.equals(a.labels.toArray(), b.labels.toArray()));
    }

    public static boolean compareMethodInsnNode(MethodInsnNode a, MethodInsnNode b) {
        return (a.owner.equals(b.owner))&&(a.desc.equals(b.desc))&&(a.name.equals(b.name));

    }

    public static boolean compareMultiANewArrayInsnNode(MultiANewArrayInsnNode a, MultiANewArrayInsnNode b) {
        return (a.dims == b.dims) && (a.desc.equals(b.desc));
    }

    public static boolean compareTableSwitchInsnNode(TableSwitchInsnNode a, TableSwitchInsnNode b) {
        return (a.dflt.getLabel().toString().equals(b.dflt.getLabel().toString()))&&(a.max == b.max)&&(a.min == b.min)&&(Arrays.equals(a.labels.toArray(), b.labels.toArray()));
    }

    public static boolean compareTypeInsnNode(TypeInsnNode a, TypeInsnNode b) {
        return (a.desc.equals(b.desc));
    }

    public static boolean compareVarInsnNode(VarInsnNode a, VarInsnNode b) {
        return (a.var == b.var);
    }

    public static boolean compareInsnNode(AbstractInsnNode a, AbstractInsnNode b) {
        if(!a.getClass().equals(b.getClass())) {
            return false;
        }
        if(a instanceof FieldInsnNode){
            return compareFieldInsnode((FieldInsnNode)a, (FieldInsnNode)b);
        } else if (a instanceof FrameNode) {
            return compareFrameNode((FrameNode)a, (FrameNode)b);
        } else if (a instanceof IincInsnNode) {
            return compareIincInsnNode((IincInsnNode)a, (IincInsnNode)b);
        } else if (a instanceof InsnNode) {
            return compareInsnNode((InsnNode)a, (InsnNode)b);
        } else if (a instanceof IntInsnNode) {
            return compareIntInsnNode((IntInsnNode)a, (IntInsnNode)b);
        } else if (a instanceof InvokeDynamicInsnNode) {
            return compareInvokeDynamicInsnNode((InvokeDynamicInsnNode)a, (InvokeDynamicInsnNode)b);
        } else if (a instanceof JumpInsnNode) {
            return compareJumpInsnNode((JumpInsnNode)a, (JumpInsnNode)b);
        } else if (a instanceof LabelNode) {
            return compareLabelNode((LabelNode)a, (LabelNode)b);
        } else if (a instanceof LdcInsnNode) {
            return compareLdcInsnNode((LdcInsnNode)a, (LdcInsnNode)b);
        } else if (a instanceof LineNumberNode) {
            return compareLineNumberNode((LineNumberNode)a, (LineNumberNode)b);
        } else if (a instanceof LookupSwitchInsnNode) {
            return compareLookupSwitchInsnNode((LookupSwitchInsnNode)a, (LookupSwitchInsnNode)b);
        } else if (a instanceof MethodInsnNode) {
            return compareMethodInsnNode((MethodInsnNode)a, (MethodInsnNode)b);
        } else if (a instanceof MultiANewArrayInsnNode) {
            return compareMultiANewArrayInsnNode((MultiANewArrayInsnNode)a, (MultiANewArrayInsnNode)b);
        } else if (a instanceof TableSwitchInsnNode) {
            return compareTableSwitchInsnNode((TableSwitchInsnNode)a, (TableSwitchInsnNode)b);
        } else if (a instanceof TypeInsnNode) {
            return compareTypeInsnNode((TypeInsnNode)a, (TypeInsnNode)b);
        } else if (a instanceof VarInsnNode) {
            return compareVarInsnNode((VarInsnNode)a, (VarInsnNode)b);
        }
        System.out.println("insnode out of list");
        return true;
    }



}
