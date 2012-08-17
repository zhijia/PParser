const scrambleComments = false;
const commentProbability = 0.05;

function randomChar(max) {
  return String.fromCharCode("a".charCodeAt() + (Math.random() * max | 0))
};

function createNode(depth) {
  var children = null;
  if (Math.random() < commentProbability) {
    return {type: 'comment'};
  }
  var type = randomChar(26);
  var count = 1 + (Math.random() * 8 | 0);
  if (count && depth) {
    children = [];
    while (count --) {
      children.push(createNode(depth - 1));
    }
  }
  return {type: type, children: children};
}

var root = createNode(8);

function traceInvalidTree(depth) {
  var str = "<" + randomChar(25) + ">";
  if (depth) {
    var count = 1 + (Math.random() * 5 | 0);
    while (count --) {
      str += traceInvalidTree(depth - 1);
    }
  }
  return str + "</" + randomChar(25) + ">";
}

function traceNode(node) {
  if (node.type === 'comment') {
    return "<!--" + traceInvalidTree(4) + "-->";
  }
  var str = "<" + node.type + ">";
  if (node.children) {
    node.children.forEach(function (x) {
      str += traceNode(x);
    });
  }
  str += "</" + node.type + ">";
  return str;
}

var str = traceNode(root);

console.info(str);
