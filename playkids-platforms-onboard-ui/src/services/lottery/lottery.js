import VueResource from 'vue-resource'

const LOTTERY_PATH = 'lottery/'

export default {
  install(Vue) {
    Vue.use(VueResource)

    const lotteryService = {

      getAllPendingLotteries() {
        this.lotteriesPromise = Vue.http.get(LOTTERY_PATH)

        return this.lotteriesPromise
      }
    }

    Vue.lotteryService = lotteryService
    Vue.prototype.$lotteryService = lotteryService
  }
}