clear_func(){
  rm -rf src/.git
}

commit_func(){
  local name=$1
  local number="${name//[^0-9]/}"
  local author=$2
  local br=$3
  local is_new_br=$4

  if [ "$is_new_br" == true ]; then
    git checkout -b "${br}"
  else
    git checkout "${br}"
  fi

  git commit --allow-empty --author="${author} <${author}@poop.us>" -m "$name"
}

merge_func(){
  local br_to=$1
  local br_from=$2
  local author=$3
  local name=$4

  git checkout "${br_to}"

  git merge  --allow-unrelated-histories --no-ff "${br_from}" -m "merged ${br_from} to ${br_to}"

}


init_func(){
  git init
  git config user.name "red"
  git config user.email "red@poop.us"
}
