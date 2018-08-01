<template>
  <div class="row-fluid">
    <div class="col-sm-4 col-sm-offset-4">
      <div class="panel panel-default">
        <div class="panel-heading">
          <h3 class="panel-title text-center">Select the Lottery to buy a Ticket</h3>
        </div>
        
        <div class="panel-body">
          <el-form :model="model" :rules="rules" ref="form">

              <el-form-item label="Lottery" prop="lottery">
                  <el-select v-model="model.lottery" placeholder="Select a Lottery">
                    <el-option v-for="l in lotteries" :key="l.id" :label="l.title" :value="l"></el-option>
                </el-select>
              </el-form-item>

              <el-form-item label="Ticket Price">
                <el-input type="number" name="ticketprice" id="ticketprice" :value="model.lottery ? model.lottery.ticketPrice : null" disabled/>
              </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="submit('form')">Buy Ticket</el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
function buyTicket() {
  this.$ticketService
    .buyTicket(this.model.lottery.id)
    .then(response => {
      this.$message.success(
        "Congratulations! You bought a ticket for " + this.model.lottery.title
      );

      this.$router.push("/");
    })
    .catch(error => {
      if (error.status === 400) {
        let prettyError = error.body
          .map(e => {
            return e.description;
          })
          .join("<br />");

        this.$message.error({
          dangerouslyUseHTMLString: true,
          message: "<b>Failed to buy ticket</b>: <br />" + prettyError,
          duration: 6000
        });
      } else if (error.status !== 401) {
        this.$message.error("Unknown error: " + error);
      }
    });
}

export default {
  name: "BuyTicket",
  data() {
    return {
      lotteries: [],
      model: {
        lottery: null
      },
      rules: {
        lottery: [
          { required: true, message: "Lottery is mandatory", trigger: "change" }
        ]
      }
    };
  },
  methods: {
    submit(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          buyTicket.call(this);
        } else {
          this.$message.error("Select a Lottery");
        }
      });
    },

    populateLotteries() {
      this.$lotteryService
        .getAllPendingLotteries()
        .then(response => {
          this.lotteries = response.body;

          this.loading = false;
        })
        .catch(error => {
          this.$message.error("Something odd happent: " + error);
        });
    }
  },
  created() {
    this.populateLotteries();
  }
};
</script>
