package zyz.free.service.leecode.solution;

import java.util.Arrays;

class Solution1 {

    public static void main(String[] args) {
        Solution1 solution1 = new Solution1();
        int[] nums = {3, 3};
        int target = 6;
        int[] ints = solution1.twoSum(nums, target);
        System.out.println(Arrays.toString(ints));
    }


    /**
     * 1
     * 两数之和
     *
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
