import {
  Pagination,
  Card,
  Dialog,
  Menu,
  Submenu,
  MenuItem,
  MenuItemGroup,
  Input,
  InputNumber,
  Radio,
  RadioGroup,
  RadioButton,
  Checkbox,
  CheckboxButton,
  CheckboxGroup,
  Switch,
  Select,
  Option,
  OptionGroup,
  Button,
  Table,
  TableColumn,
  DatePicker,
  Popover,
  Progress,
  Tooltip,
  Form,
  FormItem,
  Tabs,
  TabPane,
  Tag,
  Icon,
  Row,
  Col,
  Upload,
  Transfer,
  Collapse,
  CollapseItem,
  Loading,
  Alert,
  MessageBox,
  Message,
  Notification,
} from 'element-ui';

import 'element-ui/lib/theme-chalk/index.css';

import lang from 'element-ui/lib/locale/lang/en';
import locale from 'element-ui/lib/locale';

export default {
  Collapse,
  CollapseItem,
  /* eslint no-param-reassign: ["error", { "props": false }] */
  install(Vue) {
    locale.use(lang);

    Vue.use(Pagination);
    Vue.use(Dialog);
    Vue.use(Menu);
    Vue.use(Submenu);
    Vue.use(MenuItem);
    Vue.use(MenuItemGroup);
    Vue.use(Input);
    Vue.use(InputNumber);
    Vue.use(Card);
    Vue.use(Radio);
    Vue.use(RadioGroup);
    Vue.use(RadioButton);
    Vue.use(Checkbox);
    Vue.use(CheckboxButton);
    Vue.use(CheckboxGroup);
    Vue.use(Switch);
    Vue.use(Select);
    Vue.use(Option);
    Vue.use(OptionGroup);
    Vue.use(Button);
    Vue.use(Table);
    Vue.use(TableColumn);
    Vue.use(DatePicker);
    Vue.use(Popover);
    Vue.use(Progress);
    Vue.use(Tooltip);
    Vue.use(Form);
    Vue.use(FormItem);
    Vue.use(Tabs);
    Vue.use(TabPane);
    Vue.use(Tag);
    Vue.use(Icon);
    Vue.use(Row);
    Vue.use(Col);
    Vue.use(Upload);
    Vue.use(Transfer);
    Vue.use(Collapse);
    Vue.use(CollapseItem);
    Vue.use(Alert);

    Vue.use(Loading.directive);

    Vue.prototype.$loading = Loading.service;
    Vue.prototype.$msgbox = MessageBox;
    Vue.prototype.$alert = MessageBox.alert;
    Vue.prototype.$confirm = MessageBox.confirm;
    Vue.prototype.$prompt = MessageBox.prompt;
    Vue.prototype.$message = Message;
    Vue.prototype.$notify = Notification;

    Vue.loading = Loading.service;
    Vue.msgbox = MessageBox;
    Vue.alert = MessageBox.alert;
    Vue.confirm = MessageBox.confirm;
    Vue.prompt = MessageBox.prompt;
    Vue.message = Message;
    Vue.notify = Notification;
  },
};
