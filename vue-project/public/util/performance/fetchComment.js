import http from "..";

// 模拟评价数据
const mockComments = [
  {
    id: 1,
    performance: "周杰伦2024嘉年华世界巡回演唱会",
    rating: 5,
    content: "非常棒的演唱会！音响效果和舞台设计都超出了我的预期。",
    date: "2024-05-15"
  },
  {
    id: 2,
    performance: "五月天2024好好好想见到你演唱会",
    rating: 4,
    content: "经典歌曲现场演绎很棒，气氛很好，就是人太多有点拥挤。",
    date: "2024-04-22"
  },
  {
    id: 3,
    performance: "林俊杰2024JJ20世界巡回演唱会",
    rating: 5,
    content: "JJ的现场实力一如既往地强，每一首歌都很好听，值得推荐！",
    date: "2024-03-10"
  }
];

/**
 * 获取用户评价数据
 * @returns {Promise} 返回包含评价数据的Promise
 */
export const fetchComment = () => {
    // 模拟网络请求延迟
    return new Promise((resolve) => {
        setTimeout(() => {
            resolve({
                data: {
                    code: 200,
                    data: mockComments,
                    message: "获取评价成功"
                }
            });
        }, 500);
    });
}