package com.bitcoinpotato.util;

public interface Func2<TArg1, TArg2, TResult> {
    TResult apply(TArg1 arg1, TArg2 arg2);
}
