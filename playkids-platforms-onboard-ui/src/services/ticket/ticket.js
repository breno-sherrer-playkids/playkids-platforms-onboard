import VueResource from 'vue-resource';

const TICKET_PATH = 'ticket/';

export default {
  install(Vue) {
    Vue.use(VueResource)

    const ticketService = {

      buyTicket(idLottery) {
        this.buyTicketPromise = Vue.http.post(TICKET_PATH, { idLottery })

        return this.buyTicketPromise
      }
    }

    Vue.ticketService = ticketService
    Vue.prototype.$ticketService = ticketService
  }
}