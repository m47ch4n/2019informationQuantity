package s4.B193362; // Please modify to s4.Bnnnnnn, where nnnnnn is your student ID. 

import java.lang.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import s4.specification.*;

/*package s4.specification;
  ここは、１回、２回と変更のない外部仕様である。
  public interface FrequencerInterface {     // This interface provides the design for frequency counter.
  void setTarget(byte  target[]); // set the data to search.
  void setSpace(byte  space[]);  // set the data to be searched target from.
  int frequency(); //It return -1, when TARGET is not set or TARGET's length is zero
  //Otherwise, it return 0, when SPACE is not set or SPACE's length is zero
  //Otherwise, get the frequency of TAGET in SPACE
  int subByteFrequency(int start, int end);
  // get the frequency of subByte of taget, i.e target[start], taget[start+1], ... , target[end-1].
  // For the incorrect value of START or END, the behavior is undefined.
  }
*/

public class Frequencer implements FrequencerInterface {

    class Suffix implements Comparable<Suffix> {
        public int value;

        public Suffix(int v) {
            this.value = v;
        }

        public int compareTo(Suffix sai) {
            return suffixCompare(this.value, sai.value);
        }
    }

    // Code to start with: This code is not working, but good start point to work.
    byte[] myTarget;
    byte[] mySpace;
    boolean targetReady = false;
    boolean spaceReady = false;

    ArrayList<Suffix> suffixArray;

    // The variable, "suffixArray" is the sorted array of all suffixes of mySpace.
    // Each suffix is expressed by a integer, which is the starting position in
    // mySpace.

    // The following is the code to print the contents of suffixArray.
    // This code could be used on debugging.

    private void printSuffixArray() {
        if (spaceReady) {
            for (int i = 0; i < mySpace.length; i++) {
                int s = suffixArray.get(i).value;
                for (int j = s; j < mySpace.length; j++) {
                    System.out.write(mySpace[j]);
                }
                System.out.write('\n');
            }
        }
    }

    private int suffixCompare(int i, int j) {
        // suffixCompareはソートのための比較メソッドである。
        // 次のように定義せよ。
        // comparing two suffixes by dictionary order.
        // suffix_i is a string starting with the position i in "byte [] mySpace".
        byte[] suffix_i = Arrays.copyOfRange(mySpace, i, mySpace.length);
        // Each i and j denote suffix_i, and suffix_j.
        byte[] suffix_j = Arrays.copyOfRange(mySpace, j, mySpace.length);
        // Example of dictionary order
        // "i" < "o" : compare by code
        // "Hi" < "Ho" ; if head is same, compare the next element
        // "Ho" < "Ho " ; if the prefix is identical, longer string is big
        // "Ho " > "Ho" ;
        //
        // The return value of "int suffixCompare" is as follows.
        // if suffix_i > suffix_j, it returns 1
        // if suffix_i < suffix_j, it returns -1
        // if suffix_i = suffix_j, it returns 0;

        // ここにコードを記述せよ
        //

        for (int k = 0; k < Math.min(suffix_i.length, suffix_j.length); k ++) {
            int diff = suffix_i[k] - suffix_j[k];
            if (diff < 0) return -1;
            if (diff > 0) return 1;
        }

        if (i < j) return 1;
        if (i > j) return -1;
        return 0;
    }

    public void setSpace(byte[] space) {
        // suffixArrayの前処理は、setSpaceで定義せよ。
        mySpace = space;
        if (mySpace.length > 0)
            spaceReady = true;
        // First, create unsorted suffix array.
        suffixArray = new ArrayList<Suffix>();
        // put all suffixes in suffixArray.
        for (int i = 0; i < space.length; i++) {
            suffixArray.add(new Suffix(i)); // Please note that each suffix is expressed by one integer.
        }
        //
        // ここに、int suffixArrayをソートするコードを書け。
        // 順番はsuffixCompareで定義されるものとする。

        // Java の Comparable クラスを実装した Suffix クラスで suffix_array を構成しているため、
        // マージソートベースの O(nlog n) の Collections.sort メソッドが使用できる
        Collections.sort(suffixArray);
    }

    // Suffix Arrayを用いて、文字列の頻度を求めるコード
    // ここから、指定する範囲のコードは変更してはならない。

    public void setTarget(byte[] target) {
        myTarget = target;
        if (myTarget.length > 0)
            targetReady = true;
    }

    public int frequency() {
        if (targetReady == false)
            return -1;
        if (spaceReady == false)
            return 0;
        return subByteFrequency(0, myTarget.length);
    }

    public int subByteFrequency(int start, int end) {
        /*
         * This method be work as follows, but much more efficient int spaceLength =
         * mySpace.length; int spaceLength = mySpace.length; int spaceLength =
         * mySpace.length; int count = 0; int count = 0; int count = 0; for(int offset =
         * 0; offset< spaceLength - (end - start); offset++) { boolean abort = false;
         * boolean abort = false; boolean abort = false; for(int i = 0; i< (end -
         * start); i++) { if(myTarget[start+i] != mySpace[offset+i]) { abort = true;
         * break; } } if(abort == false) { count++; } }
         */
        if (targetReady == false)
            return -1;
        if (spaceReady == false)
            return 0;

        // System.out.println("- Suffix Array --------");
        // for (int i = 0; i < suffixArray.size(); i ++) {
        //     int compared = targetCompare(suffixArray.get(i).value, start, end);
        //     System.out.println(compared);
        // }
        // System.out.println("-----------------------");

        int find = binarySearch(start, end);
        if (find == -1) {
            return 0;
        }

        int first = subByteStartIndex(find, start, end);
        int last = subByteEndIndex(find, start, end);
        return last - first;
    }

    private int targetCompare(int i, int j, int k) {
        // suffixArrayを探索するときに使う比較関数。
        // 次のように定義せよ
        // suffix_i is a string in mySpace starting at i-th position.
        byte[] suffix_i = Arrays.copyOfRange(mySpace, i, mySpace.length);
        // target_i_k is a string in myTarget start at j-th postion ending k-th
        // position.
        byte[] target_i_k = Arrays.copyOfRange(myTarget, j, k);
        // comparing suffix_i and target_j_k.
        // if the beginning of suffix_i matches target_i_k, it return 0.
        // The behavior is different from suffixCompare on this case.
        // if suffix_i > target_i_k it return 1;
        // if suffix_i < target_i_k it return -1;
        // It should be used to search the appropriate index of some suffix.
        // Example of search
        // suffix target
        // "o" > "i"
        // "o" < "z"
        // "o" = "o"
        // "o" < "oo"
        // "Ho" > "Hi"
        // "Ho" < "Hz"
        // "Ho" = "Ho"
        // "Ho" < "Ho " : "Ho " is not in the head of suffix "Ho"
        // "Ho" = "H" : "H" is in the head of suffix "Ho"
        //
        // ここに比較のコードを書け
        //

        for (int l = 0; l < Math.min(suffix_i.length, target_i_k.length); l ++) {
            int diff = suffix_i[l] - target_i_k[l];
            if (diff < 0) return -1;
            if (diff > 0) return 1;
        }

        if (suffix_i.length < target_i_k.length) return -1;
        return 0;
    }

    private int binarySearch(int start, int end) {
        return binarySearch(0, suffixArray.size() - 1, start, end);
    }

    private int binarySearch(int low, int high, int start, int end) {
        if (high < low)
            return -1;

        int middle = (low + high) / 2;
        int ret = targetCompare(suffixArray.get(middle).value, start, end);

        if (ret > 0)
            return binarySearch(low, middle - 1, start, end);
        else if (ret <  0)
            return binarySearch(middle + 1, high, start, end);

        return middle;
    }

    private int subByteStartIndex(int find, int start, int end) {
        // suffix arrayのなかで、目的の文字列の出現が始まる位置を求めるメソッド
        // 以下のように定義せよ。
        /*
         * Example of suffix created from "Hi Ho Hi Ho" 0: Hi Ho 1: Ho 2: Ho Hi Ho 3:Hi
         * Ho 4:Hi Ho Hi Ho 5:Ho 6:Ho Hi Ho 7:i Ho 8:i Ho Hi Ho 9:o A:o Hi Ho
         */

        // It returns the index of the first suffix
        // which is equal or greater than target_start_end.
        // Assuming the suffix array is created from "Hi Ho Hi Ho",
        // if target_start_end is "Ho", it will return 5.
        // Assuming the suffix array is created from "Hi Ho Hi Ho",
        // if target_start_end is "Ho ", it will return 6.
        //
        // ここにコードを記述せよ。
        //
        int i = 0, j = find;
        while (i <= j) {
            int x = (i + j) / 2;
            int ret = targetCompare(suffixArray.get(x).value, start, end);
            if (ret > 0) j = x - 1;
            else if (ret < 0) i = x + 1;
            else if (x == 0) return x;
            else if (targetCompare(suffixArray.get(x-1).value, start, end) != 0) return x;
            else j = x - 1;
        }
        return -1;
    }

    private int subByteEndIndex(int find, int start, int end) {
        // suffix arrayのなかで、目的の文字列の出現しなくなる場所を求めるメソッド
        // 以下のように定義せよ。
        /*
         * Example of suffix created from "Hi Ho Hi Ho" 0: Hi Ho 1: Ho 2: Ho Hi Ho 3:Hi
         * Ho 4:Hi Ho Hi Ho 5:Ho 6:Ho Hi Ho 7:i Ho 8:i Ho Hi Ho 9:o A:o Hi Ho
         */
        // It returns the index of the first suffix
        // which is greater than target_start_end; (and not equal to target_start_end)
        // Assuming the suffix array is created from "Hi Ho Hi Ho",
        // if target_start_end is "Ho", it will return 7 for "Hi Ho Hi Ho".
        // Assuming the suffix array is created from "Hi Ho Hi Ho",
        // if target_start_end is"i", it will return 9 for "Hi Ho Hi Ho".
        //
        // ここにコードを記述せよ
        //
        int i = find, j = suffixArray.size()-1;
        while (i <= j) {
            int x = (i + j) / 2;
            int ret = targetCompare(suffixArray.get(x).value, start, end);
            if (ret > 0) j = x - 1;
            else if (ret < 0) i = x + 1;
            else if (x+1 == suffixArray.size()) return suffixArray.size();
            else if (targetCompare(suffixArray.get(x+1).value, start, end) != 0) return x + 1;
            else i = x + 1;
        }
        return -1;
    }

    // Suffix Arrayを使ったプログラムのホワイトテストは、
    // privateなメソッドとフィールドをアクセスすることが必要なので、
    // クラスに属するstatic mainに書く方法もある。
    // static mainがあっても、呼びださなければよい。
    // 以下は、自由に変更して実験すること。
    // 注意：標準出力、エラー出力にメッセージを出すことは、
    // static mainからの実行のときだけに許される。
    // 外部からFrequencerを使うときにメッセージを出力してはならない。
    // 教員のテスト実行のときにメッセージがでると、仕様にない動作をするとみなし、
    // 減点の対象である。
    public static void main(String[] args) {
        Frequencer frequencerObject;
        try {
            frequencerObject = new Frequencer();
            frequencerObject.setSpace("Hi Ho Hi Ho".getBytes());
            frequencerObject.printSuffixArray(); // you may use this line for DEBUG
            /*
             * Example from "Hi Ho Hi Ho" 0: Hi Ho 1: Ho 2: Ho Hi Ho 3:Hi Ho 4:Hi Ho Hi Ho
             * 5:Ho 6:Ho Hi Ho 7:i Ho 8:i Ho Hi Ho 9:o A:o Hi Ho
             */

            frequencerObject.setTarget("H".getBytes());
            //
            // **** Please write code to check subByteStartIndex, and subByteEndIndex
            //

            int result = frequencerObject.frequency();
            System.out.print("Freq = " + result + " ");
            if (4 == result) {
                System.out.println("OK");
            } else {
                System.out.println("WRONG");
            }
        } catch (Exception e) {
            System.out.println("STOP");
        }
    }

}
