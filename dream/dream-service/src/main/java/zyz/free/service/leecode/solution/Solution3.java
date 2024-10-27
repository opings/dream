package zyz.free.service.leecode.solution;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

class Solution3 {

    public static void main(String[] args) {
        Solution3 solution3 = new Solution3();
        int i = solution3.lengthOfLongestSubstring("abcabcbb");
        System.out.println(i);
    }


    /**
     * 3
     * 无重复字符的最长子串
     * @param s
     * @return
     */
    public int lengthOfLongestSubstring(String s) {
        //滑动窗口
        char[] ss = s.toCharArray();
        Set<Character> set = new HashSet<>();//去重
        int res = 0;//结果
        for (int left = 0, right = 0; right < s.length(); right++) {//每一轮右端点都扩一个。
            char ch = ss[right];//right指向的元素，也是当前要考虑的元素
            while (set.contains(ch)) {//set中有ch，则缩短左边界，同时从set集合出元素
                set.remove(ss[left]);
                left++;
            }
            set.add(ss[right]);//别忘。将当前元素加入。
            res = Math.max(res, right - left + 1);//计算当前不重复子串的长度。
        }
        return res;

    }




    /**
     * 1
     *  两数之和
     * @param nums
     * @param target
     * @return
     */
    public int[] twoSum(int[] nums, int target) {
        int[] index = new int[2];
        for (int i = 0; i < nums.length; i++) {
            for (int i1 = i + 1; i1 < nums.length; i1++) {
                int count = nums[i] + nums[i1];
                if (count == target) {
                    index[0] = i;
                    index[1] = i1;
                    break;
                }
            }
        }
        return index;
    }
}
