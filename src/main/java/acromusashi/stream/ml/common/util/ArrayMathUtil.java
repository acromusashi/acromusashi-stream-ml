/**
* Copyright (c) Acroquest Technology Co, Ltd. All Rights Reserved.
* Please read the associated COPYRIGHTS file for more details.
*
* THE SOFTWARE IS PROVIDED BY Acroquest Technolog Co., Ltd.,
* WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
* BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
* IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDER BE LIABLE FOR ANY
* CLAIM, DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
* OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
*/
package acromusashi.stream.ml.common.util;

/**
 * 配列に対して計算を行うユーティリティクラス
 * 
 * @author kimura
 */
public class ArrayMathUtil
{
    /**
     * インスタンス化を防止するためのコンストラクタ
     */
    private ArrayMathUtil()
    {}

    /**
     * 配列の各要素に対して指定した引数の乗算を行う。
     * 
     * @param base ベース配列
     * @param multi 乗算を行う数
     * @return 乗算結果配列
     */
    public static double[] multiArray(double[] base, double multi)
    {
        int length = base.length;
        double[] result = new double[length];
        for (int i = 0; i < length; i++)
        {
            result[i] = base[i] * multi;
        }
        return result;
    }

    /**
     * 配列の各要素の加算を行う。
     * 
     * @param array1 配列1
     * @param array2 配列2
     * @return (配列1 + 配列2)の結果配列
     */
    public static double[] addArray(double[] array1, double[] array2)
    {
        if (array1.length != array2.length)
        {
            throw new IllegalArgumentException("The dimensions have to be equal!");
        }

        double[] result = new double[array1.length];
        assert array1.length == array2.length;
        for (int i = 0; i < array1.length; i++)
        {
            result[i] = array1[i] + array2[i];
        }

        return result;
    }

    /**
     * 配列の各要素の減算を行う。
     * 
     * @param array1 配列1
     * @param array2 配列2
     * @return (配列1 - 配列2)の結果配列
     */
    public static double[] subtractArray(double[] array1, double[] array2)
    {
        if (array1.length != array2.length)
        {
            throw new IllegalArgumentException("The dimensions have to be equal!");
        }

        double[] result = new double[array1.length];
        assert array1.length == array2.length;
        for (int i = 0; i < array1.length; i++)
        {
            result[i] = array1[i] - array2[i];
        }

        return result;
    }
}
