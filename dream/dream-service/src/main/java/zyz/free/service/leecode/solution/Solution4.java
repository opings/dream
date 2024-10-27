package zyz.free.service.leecode.solution;

import java.util.HashSet;
import java.util.Set;

class Solution4 {

    public static void main(String[] args) {
        Solution4 solution = new Solution4();
//        int[] nums = {1, 3, 4, 5, 6, 8, 9, 11};
//        int target = 11;
//        int solution1 = solution.solution(nums, target);
//        System.out.println(solution1);

//        int[] nums = {3,2,3,2,3};
//        int[] nums = {7,4,-2,4,-2,-9};
        int[] nums = {7,-5,-5,-5,7,-1,7};
        int solution1 = solution.solution2(nums);
        System.out.println(solution1);

//        ConcurrentSkipListMap<Object, Object> objectObjectConcurrentSkipListMap = new ConcurrentSkipListMap<>();
//        objectObjectConcurrentSkipListMap.put()
//        objectObjectConcurrentSkipListMap.get()


    }


    public int solution2(int[] a) {
        int length = a.length;

        if (length == 1) {
            return 1;
        }
        if (length == 2) {
            return 2;
        }


        int maxCount = 2;
        int left = 0;
        int right = 2;
        for (right = 2; right < length; right++) {
            if (a[right] == a[right - 2]) {
                int tempMaxCount = right - left + 1;
                maxCount = Math.max(tempMaxCount, maxCount);
            } else {
                left = right - 1;
            }

        }
        return maxCount;
    }


    public int solution1(String s) {
        char[] numArr = s.toCharArray();
        Set<Integer> set = new HashSet<>();

        // 总数
        int count = 0;
        // 初始值大小
        int originSum = 0;
        for (int i = 0; i < numArr.length; i++) {
            originSum = originSum + ((numArr[i] - 48) * (int) Math.pow(10, numArr.length - 1 - i));
        }

        for (int i = 0; i < numArr.length; i++) {
            // 减去需要修改的值
            int newSum = originSum - ((numArr[i] - 48) * (int) Math.pow(10, numArr.length - 1 - i));
            for (int j = 0; j <= 9; j++) {
                // 每次替换一位数
                int newSum1 = newSum + (j * (int) Math.pow(10, numArr.length - 1 - i));
                if (newSum1 % 3 == 0 && set.add(newSum1)) {
                    count++;
                }
            }
        }
        return count;
    }


    int solution(int[] A, int X) {
        int N = A.length;
        if (N == 0) {
            return -1;
        }
        int l = 0;
        int r = N - 1;
        while (l < r) {
            int m = (l + r) / 2 + 1;
            if (A[m] > X) {
                r = m - 1;
            } else {
                l = m;
            }
        }
        if (A[l] == X) {
            return l;
        }
        return -1;
    }


}
