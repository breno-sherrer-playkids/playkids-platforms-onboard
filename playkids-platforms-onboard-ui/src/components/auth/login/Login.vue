<template>
  <div id="auth" class="row-fluid">
    <div class="col-sm-4 col-sm-offset-4">
      <div class="panel panel-default">
        <div class="panel-heading">
          <h3 class="panel-title text-center">Welcome!</h3>
        </div>
        
        <div class="panel-body">
          <el-form class="form-signin" :model="auth" :rules="rules" ref="form">

              <el-form-item label="E-Mail" prop="email">
                <el-input type="text" name="email" id="email" value="" v-model="auth.email" />
              </el-form-item>

              <el-form-item label="Password" prop="password">
                <el-input type="password" name="password" id="password"  value="" v-model="auth.password" 
                @keyup.enter.native="submit('form')" />
              </el-form-item>

            <el-form-item>
              <el-button type="primary" name="login_button" @click="submit('form')">Login</el-button>
            </el-form-item>

            <el-form-item>
              Don't have an account? <router-link to="/signup">Create one now!</router-link>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
function authenticate() {
  this.$authenticationService
    .signIn(this.auth.email, this.auth.password)
    .then(response => {
      // If login succeds, consumes the victory message and displays it
      this.$userService
        .consumeCongratulations()
        .then(response => {
          if (response.body.congratulate) {
            this.$message.success({
              message: "Congratulations!",
              duration: 5000
            });
          }
        })
        .catch(error => {
          this.$message.error("Fatal error, contact the support: " + error);
        });

      this.$router.push("/");
    })
    .catch(error => {
      if (error.status === 401) {
        this.$message.error("Invalid credentials!");
      } else {
        this.$message.error(
          "Unknown error, please contact the support at breno.sherrer@playkids.com"
        );
      }
    });
}

export default {
  name: "auth",
  data() {
    return {
      auth: {
        email: "breno.sherrer@playkids.com",
        password: "brenaobreninho"
      },
      rules: {
        email: [
          { required: true, message: "E-mail is mandatory", trigger: "blur" }
        ],
        password: [
          { required: true, message: "Password is mandatory", trigger: "blur" }
        ]
      }
    };
  },
  methods: {
    submit(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          authenticate.call(this);
        } else {
          this.$message.error("Invalid field username and/or password");
        }
      });
    }
  }
};
</script>
<style lang="scss" scoped>
</style>
