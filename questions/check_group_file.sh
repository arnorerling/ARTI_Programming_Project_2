#!/bin/bash

. ./setupeditor.sh

groupfile=answers/group.txt
groupfilepath=../$groupfile
course=$(cat ../.course)
group_for_class=ru-course-$course

correct=false
count=0
while [[ $correct == "false" && $count -lt 2 ]] ; do
	correct=true
	if [[ -f $groupfilepath ]] ; then
		echo "group members: "
		empty=true
		for x in $(cat $groupfilepath | grep -v -E '^#' | grep -v -E '^$') ; do
			empty=false
			if ! getent group $group_for_class | sed -e 's/.*://g' -e 's/,/\n/g' | grep "$x" &> /dev/null; then
				echo
				echo "Invalid username '$x' in $groupfile!"
				echo "Press ENTER to continue!"
				read answer
				correct=false
			else
				echo "$x"
			fi
		done
		echo
		if [[ $empty == "true" ]] ; then
			echo "No username in $groupfile!"
			correct=false
		elif [[ $correct == "true" ]] ; then
			echo "Are these the correct group members (y/n)?"
			read -n 1 yn
			echo
			if [[ "$yn" != "y" ]]; then
				correct=false
			fi

		fi
	else
		echo '# Enter the usernames of all group members below, one username per line and save this file.' > $groupfilepath
		echo $USER >> $groupfilepath
		echo "Did you work on the assignment alone (y/n)?"
		read -n 1 yn
		echo
		if [[ "$yn" == "y" ]]; then
			correct=true
		else
			correct=false
		fi
	fi
	if [[ $correct == false ]]; then
		"$EDITOR" $groupfilepath
	fi
	count=$((count+1))
done

if [[ $correct == "false" ]]; then
	echo "Correct the usernames in $groupfile and re-run 'make handin' to make the handin!"
	exit -1
fi
